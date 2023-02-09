package si.example.rabbitmqcustom.declared;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import si.example.rabbitmqcustom.processing.annotation.*;
import si.example.rabbitmqcustom.processing.annotation.util.TypeExchange;

@Configuration
@RabbitMqInjection(
        queues = {
                @RabbitQueue(name = "proxy.queue.one"),
                @RabbitQueue(name = "proxy.queue.tow"),
                @RabbitQueue(name = "proxy.queue.three")
        },
        binding = {
                @RabbitBinding(queue = @RabbitQueue(name = "rabbit.mail"),
                        exchange = @RabbitExchange(name = "exchange.mail", type = TypeExchange.TOPIC)
                ),
                @RabbitBinding(queue = @RabbitQueue(name = "bot.${env}"),
                        exchange = @RabbitExchange(name = "exchange.bot.${env}", type = TypeExchange.HEADERS),
                        arguments = {
                                @Argument(key = "envName", value = "${env}")
                        }
                ),
        })
public class ConfigurationProxyExample {

        @Value("rabbit.si.queue.clasification")
        @RabbitQueue
        private String queueName;
}
