package si.example.rabbitmqcustom.declared;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import si.example.rabbitmqcustom.processing.annotation.*;
import si.example.rabbitmqcustom.processing.annotation.util.TypeExchange;

@RabbitMqInjection(
        queues = {
                @RabbitQueue(name = "callback.${env}"),
                @RabbitQueue(name = "billing.${env}")
        },
        binding = {
                @RabbitBinding(queue = @RabbitQueue(name = "request.${env}", arguments = {
                        @Argument(key = "x-dead-letter-exchange", value = ""),
                        @Argument(key = "x-dead-letter-routing-key", value = "callback.${env}"),
                }),
                        exchange = @RabbitExchange(name = "request.exchange.${env}", type = TypeExchange.X_DELAYED_MESSAGE)
                )
        })
@Component
public class ServiceExample {

        @RabbitBinding(queue = @RabbitQueue,
                exchange = @RabbitExchange(name = "check", type = TypeExchange.HEADERS),
                arguments = {
                        @Argument(key = "envName", value = "${env}")
                })
        @Value("rabbit.si.queue.bill.check")
        private String billCheck;
}
