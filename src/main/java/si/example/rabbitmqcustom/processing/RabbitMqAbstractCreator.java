package si.example.rabbitmqcustom.processing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import si.example.rabbitmqcustom.processing.annotation.Argument;
import si.example.rabbitmqcustom.processing.annotation.RabbitBinding;
import si.example.rabbitmqcustom.processing.annotation.RabbitExchange;
import si.example.rabbitmqcustom.processing.annotation.RabbitQueue;
import si.example.rabbitmqcustom.processing.annotation.util.Durable;
import si.example.rabbitmqcustom.processing.annotation.util.TypeExchange;
import si.example.rabbitmqcustom.processing.annotation.util.TypeQueue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public abstract class RabbitMqAbstractCreator {
    @Autowired
    private AmqpAdmin amqpAdmin;
    @Value("${si.example.env}")
    private String env;
    private final String REGEX_ENV = "(?i)\\$\\{env\\}";

    protected void createQueue(RabbitQueue rabbitQueue) {
        try {
            createQueue(new RabbitQueueDeclared(rabbitQueue));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void createQueue(RabbitQueue rabbitQueue, Field field, Object bean) {
        try {
            createQueue(new RabbitQueueDeclared(rabbitQueue, field, bean));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void createExchange(RabbitExchange rabbitExchange) {
        try {
            createExchange(new RabbitExchangeDeclared(rabbitExchange));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void createBiding(RabbitBinding rabbitBinding) {
        try {
            createBiding(new RabbitBidingDeclared(rabbitBinding));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    protected void createBiding(RabbitBinding rabbitBinding, Field field, Object bean) {
        try {
            createBiding(new RabbitBidingDeclared(rabbitBinding, field, bean));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void createQueue(RabbitQueueDeclared rabbitQueueDeclared) {
        if (isNull(amqpAdmin.getQueueInfo(rabbitQueueDeclared.getName()))) {
            Map<String, Object> args = rabbitQueueDeclared.getArguments();
            args.put("x-queue-type", rabbitQueueDeclared.getType().name().toLowerCase());

            amqpAdmin.declareQueue(new Queue(rabbitQueueDeclared.getName(),
                    rabbitQueueDeclared.isDurable(),
                    rabbitQueueDeclared.isExclusive(),
                    rabbitQueueDeclared.isAutoDelete(),
                    args));
        }
    }

    private void createExchange(RabbitExchangeDeclared rabbitExchangeDeclared) {
        Map<String, Object> args = rabbitExchangeDeclared.getArguments();

        switch (rabbitExchangeDeclared.getType()) {
            case DIRECT:
                break;

            case TOPIC:
                break;

            case FANOUT:
                break;

            case HEADERS:
                break;

            case X_DELAYED_MESSAGE:
                if (!args.containsKey("x-delayed-type")) {
                    args.put("x-delayed-type", "direct");
                }
                break;
        }

        Exchange exchange = new CustomExchange(rabbitExchangeDeclared.getName(),
                rabbitExchangeDeclared.getType().getValue(),
                rabbitExchangeDeclared.isDurable(),
                rabbitExchangeDeclared.isAutoDelete(),
                args);

        amqpAdmin.declareExchange(exchange);
    }

    private void createBiding(RabbitBidingDeclared rabbitBidingDeclared) {
        for (RabbitQueueDeclared rabbitQueueDeclared : rabbitBidingDeclared.getQueue()) {
            createQueue(rabbitQueueDeclared);
        }

        createExchange(rabbitBidingDeclared.getExchange());


        for (RabbitQueueDeclared rabbitQueueDeclared : rabbitBidingDeclared.getQueue()) {
            Binding binding = new Binding(rabbitQueueDeclared.getName(),
                    rabbitBidingDeclared.getType(),
                    rabbitBidingDeclared.getExchange().getName(),
                    rabbitBidingDeclared.getRoutingKey(),
                    rabbitBidingDeclared.getArguments());

            amqpAdmin.declareBinding(binding);
        }
    }

    private String injectFiledValue(String value, Field field, Object bean) throws IllegalAccessException {
        return value.equals("#autoInject") ? getValueFromFiled(field, bean) : value;
    }

    private String getValueFromFiled(Field field, Object bean) throws IllegalAccessException {
        field.setAccessible(true);
        String value = (String) field.get(bean);
        field.setAccessible(false);

        return value;
    }

    private Map<String, Object> mapToArguments(Argument[] arguments) {
        Map<String, Object> argumentsMap = new HashMap<>();
        for (Argument argument : arguments) {
            argumentsMap.put(replaceInv(argument.key()), replaceInv(argument.value()));
        }

        return argumentsMap;
    }

    private String replaceInv(String value) {
        return value.replaceAll(REGEX_ENV, env);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    protected class RabbitQueueDeclared {
        private String name;
        private TypeQueue type;
        private Durable durable;
        private boolean autoDelete;
        private boolean exclusive;
        private Map<String, Object> arguments;

        public RabbitQueueDeclared(RabbitQueue rabbitQueue) {
            this.name = replaceInv(rabbitQueue.name());
            this.type = rabbitQueue.type();
            this.durable = rabbitQueue.durable();
            this.autoDelete = rabbitQueue.autoDelete();
            this.exclusive = rabbitQueue.exclusive();
            this.arguments = mapToArguments(rabbitQueue.arguments());
        }

        public RabbitQueueDeclared(RabbitQueue rabbitQueue, Field field, Object bean) throws IllegalAccessException {
            String injectedValue = injectFiledValue(rabbitQueue.name(), field, bean);
            this.name = replaceInv(injectedValue);
            this.type = rabbitQueue.type();
            this.durable = rabbitQueue.durable();
            this.autoDelete = rabbitQueue.autoDelete();
            this.exclusive = rabbitQueue.exclusive();
            this.arguments = mapToArguments(rabbitQueue.arguments());
        }

        public boolean isDurable() {
            return durable == Durable.DURABLE;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    protected class RabbitExchangeDeclared {
        private String name;
        private TypeExchange type;
        private Durable durable;
        private boolean autoDelete;
        private boolean internal;
        private Map<String, Object> arguments;

        public RabbitExchangeDeclared(RabbitExchange rabbitExchange) {
            this.name = replaceInv(rabbitExchange.name());
            this.type = rabbitExchange.type();
            this.durable = rabbitExchange.durable();
            this.autoDelete = rabbitExchange.autoDelete();
            this.internal = rabbitExchange.internal();
            this.arguments = mapToArguments(rabbitExchange.arguments());
        }

        public boolean isDurable() {
            return durable == Durable.DURABLE;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    protected class RabbitBidingDeclared {
        private List<RabbitQueueDeclared> queue = new LinkedList<>();
        private Binding.DestinationType type;
        private RabbitExchangeDeclared exchange;
        private String routingKey;
        private Map<String, Object> arguments;

        public RabbitBidingDeclared(RabbitBinding rabbitBinding) {
            for (RabbitQueue rabbitQueue : rabbitBinding.queue()) {
                this.queue.add(new RabbitQueueDeclared(rabbitQueue));
            }
            this.type = rabbitBinding.type();
            this.exchange = new RabbitExchangeDeclared(rabbitBinding.exchange());
            this.routingKey = rabbitBinding.routingKey();
            this.arguments = mapToArguments(rabbitBinding.arguments());
        }

        public RabbitBidingDeclared(RabbitBinding rabbitBinding, Field field, Object bean) throws IllegalAccessException {
            for (RabbitQueue rabbitQueue : rabbitBinding.queue()) {
                this.queue.add(new RabbitQueueDeclared(rabbitQueue, field, bean));
            }
            this.type = rabbitBinding.type();
            this.exchange = new RabbitExchangeDeclared(rabbitBinding.exchange());
            this.routingKey = rabbitBinding.routingKey();
            this.arguments = mapToArguments(rabbitBinding.arguments());
        }
    }

    @AllArgsConstructor
    @Getter
    protected static class BeanDefinition {
        private Object bean;
        private Class<?> supperClass;

        public final Class<?> getDefaultClass() {
            return isNull(supperClass) ? bean.getClass() : supperClass;
        }
    }
}