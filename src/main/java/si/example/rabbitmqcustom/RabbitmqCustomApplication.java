package si.example.rabbitmqcustom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import si.example.rabbitmqcustom.processing.annotation.RabbitMqInjection;
import si.example.rabbitmqcustom.processing.annotation.RabbitQueue;

@SpringBootApplication
@RabbitMqInjection(queues = {
		@RabbitQueue(name = "boot-queue")
})
public class RabbitmqCustomApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitmqCustomApplication.class, args);
	}

}
