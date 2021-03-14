package pram.ms.userservice.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

    public static final String MS_USER_SERVICE_QUEUE = "ms.user.service.queue";
    public static final String MS_USER_SERVICE_EXCHANGE_DIRECT = "ms.user.service.exchange.direct";
    public static final String ROUTING_KEY = "ms.user.service.routing";

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    Queue queue() {
        return new Queue(MS_USER_SERVICE_QUEUE, false);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(MS_USER_SERVICE_EXCHANGE_DIRECT);
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
}
