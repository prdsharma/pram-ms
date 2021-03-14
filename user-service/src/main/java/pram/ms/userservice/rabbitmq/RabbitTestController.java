package pram.ms.userservice.rabbitmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RabbitTestController {

    @Autowired
    RabbitQueueService rabbitQueueService;

    @PostMapping("/rabbit")
    public void saveData(@RequestBody SomeDataObj data) {
        // Sending to queue exchange
        rabbitQueueService.send(data);
    }

}
