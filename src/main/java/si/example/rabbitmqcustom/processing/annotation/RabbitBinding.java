package si.example.rabbitmqcustom.processing.annotation;

import org.springframework.amqp.core.Binding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.springframework.amqp.core.Binding.DestinationType.QUEUE;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface RabbitBinding {
    RabbitQueue[] queue();
    Binding.DestinationType type() default QUEUE;
    RabbitExchange exchange();
    String routingKey() default "";
    Argument[] arguments() default {};
}
