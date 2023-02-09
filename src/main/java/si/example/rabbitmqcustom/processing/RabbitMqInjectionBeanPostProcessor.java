package si.example.rabbitmqcustom.processing;

import si.example.rabbitmqcustom.processing.annotation.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Field;

@Component
@Order(0)
public class RabbitMqInjectionBeanPostProcessor extends RabbitMqAbstractCreator implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        try {
            if (bean.getClass().isAnnotationPresent(RabbitMqInjection.class)) {
                rabbitMqInjection(new BeanDefinition(bean, null));
            } else if (ClassUtils.getUserClass(bean).isAnnotationPresent(RabbitMqInjection.class)) {
                rabbitMqInjection(new BeanDefinition(bean, ClassUtils.getUserClass(bean)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bean;
    }

    private void rabbitMqInjection(BeanDefinition beanDefinition) {
        try {
            RabbitMqInjection rabbitMqInjection = beanDefinition.getDefaultClass().getAnnotation(RabbitMqInjection.class);
            for (RabbitQueue queue : rabbitMqInjection.queues()) {
                createQueue(queue);
            }

            for (RabbitExchange exchange : rabbitMqInjection.exchange()) {
                createExchange(exchange);
            }

            for (RabbitBinding binding : rabbitMqInjection.binding()) {
                createBiding(binding);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Field declaredField : beanDefinition.getDefaultClass().getDeclaredFields()) {
            try {
                if (declaredField.isAnnotationPresent(RabbitQueue.class)) {
                    RabbitQueue rabbitQueue = declaredField.getAnnotation(RabbitQueue.class);

                    createQueue(rabbitQueue, declaredField, beanDefinition.getBean());
                }

                if (declaredField.isAnnotationPresent(RabbitExchange.class)) {
                    RabbitExchange rabbitExchange = declaredField.getAnnotation(RabbitExchange.class);

                    createExchange(rabbitExchange);
                }

                if (declaredField.isAnnotationPresent(RabbitBinding.class)) {
                    RabbitBinding rabbitBinding = declaredField.getAnnotation(RabbitBinding.class);

                    createBiding(rabbitBinding, declaredField, beanDefinition.getBean());
                }

            } finally {
                declaredField.setAccessible(false);
            }
        }
    }
}