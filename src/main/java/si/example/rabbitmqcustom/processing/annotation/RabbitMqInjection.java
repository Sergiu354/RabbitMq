package si.example.rabbitmqcustom.processing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RabbitMqInjection {
    RabbitQueue[] queues() default {};
    RabbitExchange[] exchange() default {};
    RabbitBinding[] binding() default {};
}
