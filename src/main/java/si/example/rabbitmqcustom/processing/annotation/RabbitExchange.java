package si.example.rabbitmqcustom.processing.annotation;

import si.example.rabbitmqcustom.processing.annotation.util.Durable;
import si.example.rabbitmqcustom.processing.annotation.util.TypeExchange;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface RabbitExchange {
    String name();
    TypeExchange type();
    Durable durable() default Durable.DURABLE;
    boolean autoDelete() default false;
    boolean internal() default false;
    Argument[] arguments() default {};
}
