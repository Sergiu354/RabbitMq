## RabbitMq Custom Annotation
In this documentation shows about how to use custom annotation.
There are 4 new types of annotations for work which are explained below.

- The first annotation that starts the generation and indicates the class is @RabbitMqInjection.
This annotation indicates that in this class or in the body of the annotation there will be hints to create "Queue, Exchange, Binding"
#### The body of the annotation:
```java
public @interface RabbitMqInjection {
        RabbitQueue[] queues() default {};
        RabbitExchange[] exchange() default {};
        RabbitBinding[] binding() default {};
}
```
#### Example:
```java
@RabbitMqInjection
public class ApiRabbitMq {
    ...
}
```

```java
@RabbitMqInjection(
        queues = {
                @RabbitQueue(name = "orchestrator.training.${env}"),
                @RabbitQueue(name = "qb.create.bill.${env}")
        },
        binding = {
                @RabbitBinding(queue = @RabbitQueue(name = "document.classification.request.${env}", arguments = {
                        @Argument(key = "x-dead-letter-exchange", value = ""),
                        @Argument(key = "x-dead-letter-routing-key", value = "document.classification.request.dl.${env}"),
                }),
                        exchange = @RabbitExchange(name = "document.classification.${env}", type = TypeExchange.X_DELAYED_MESSAGE)
                )
        })
public class ApiRabbitMq {
…
}
```

For automated inject environment in the name need add
`${env}`
```java
@RabbitQueue(name = "orchestrator.training.${env}") //-> name = orchestrator.training.dev
```

- The annotation @RabbitQueue can also be indicated on a file. This annotation will create a queue.
  This annotation can automatically read the value from the field, it is entered in and insert it into the name of the queue that will be created, 
if the named parameter is, the indicated name will be entered.

#### The body of the annotation:
```java
public @interface RabbitQueue {
        String name() default "#autoInject";
        TypeQueue type() default TypeQueue.QUORUM;
        Durable durable() default Durable.DURABLE;
        boolean exclusive() default false;
        boolean autoDelete() default false;
        Argument[] arguments() default {};
        }
```
#### Example:
```java
@RabbitMqInjection
public class ApiRabbitMq {
    @RabbitQueue
    private final String queueName = "test.queue";
}  // In this example, a queue will be created with the name from the field
```

```java
@RabbitMqInjection
public class ApiRabbitMq {
    @RabbitQueue(name = "my.queue")
    private final String queueName = "test.queue";
}  // In this example, a queue will be created with the name from "my.queue"
```

```java
@RabbitMqInjection
public class ApiRabbitMq {
	@RabbitQueue(arguments = {
            	@Argument(key = "x-dead-letter-exchange", value = "orchestrator.training.callback.dlx")
    	})
	private final String queueName = "test.queue";
}  // In this example, a queue will be created with the name from the field and the argument "x-dead-letter-exchange" will be added
```


- The annotation @RabbitExchange can also be indicated on a field, but the name and type must be indicated by the user.

#### The body of the annotation:
```java
public @interface RabbitExchange {
          String name();
          TypeExchange type();
          Durable durable() default Durable.DURABLE;
          boolean autoDelete() default false;
          boolean internal() default false;
          Argument[] arguments() default {};
        }
```
#### Example:
```java
@RabbitMqInjection
public class ApiRabbitMq {

  @RabbitExchange(name = "my.exchange", type = TypeExchange.TOPIC)
  private final String queueName = "test.queue";
}  // In this example, an exchange will be created with the name "my.exchange" and the type "topic"
```

- The annotation @RabbitBinding creates a binding between queue and exchange, it can also be indicated on a file. 
The value from the indicated field will be automatically added to the name in the queue.

#### The body of the annotation:
```java
public @interface RabbitExchange {
        String name();
        TypeExchange type();
        Durable durable() default Durable.DURABLE;
        boolean autoDelete() default false;
        boolean internal() default false;
        Argument[] arguments() default {};
        }
```
#### Example:
```java
@RabbitMqInjection
public class ApiRabbitMq {
	
	@RabbitBinding(queue = @RabbitQueue(arguments = {
            @Argument(key = "x-dead-letter-exchange", value = "orchestrator.training.callback.dlx"),
    }),
            exchange = @RabbitExchange(name = "orchestrator.training.callback.dlx", type = TypeExchange.HEADERS),
            arguments = {
                    @Argument(key = "tenantId", value = "${env}")
            })
	private final String queueName = "test.queue";
} // In this example, a queue will be created that will automatically take the name from the file and an exchange, and the connection will be made between them.
```
