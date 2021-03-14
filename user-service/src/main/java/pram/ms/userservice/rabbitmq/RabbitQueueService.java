package pram.ms.userservice.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitQueueService {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void send(SomeDataObj data) {
        logger.info("Sending object to queue.........");
        amqpTemplate.convertAndSend(RabbitmqConfig.MS_USER_SERVICE_EXCHANGE_DIRECT,
                RabbitmqConfig.ROUTING_KEY, data);
    }
}
