package si.example.rabbitmqcustom.processing.annotation;

import si.example.rabbitmqcustom.processing.annotation.util.Durable;
import si.example.rabbitmqcustom.processing.annotation.util.TypeQueue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface RabbitQueue {
    String name() default "#autoInject";
    TypeQueue type() default TypeQueue.QUORUM;
    Durable durable() default Durable.DURABLE;
    boolean exclusive() default false;
    boolean autoDelete() default false;
    Argument[] arguments() default {};
}
