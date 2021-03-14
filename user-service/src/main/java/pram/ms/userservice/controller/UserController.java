package pram.ms.userservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pram.ms.userservice.dao.UserDao;
import pram.ms.userservice.exception.UserNotFoundException;
import pram.ms.userservice.kafka.KafkaService;
import pram.ms.userservice.model.User;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Locale;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class UserController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private KafkaService kafkaService;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userDao.getAll();
    }

    @GetMapping("/hello-international")
    public String hello(@RequestHeader(value = "Accept-Language", required = false) Locale locale) {
        return messageSource.getMessage("good.morning.message", null,  locale);
    }

    @GetMapping("/hello-international1")
    public String hello() {
        return messageSource.getMessage("good.morning.message", null, LocaleContextHolder.getLocale());
    }

    @PostMapping("/kafka/{topic}")
    public void kafkaProduce(@PathVariable String topic, @RequestParam("key") String key,
                             @RequestParam("message") String message) {
        kafkaService.produce(topic, key, message);
    }

    @GetMapping("/kafka/{topic}")
    public List<String> kafkaConsume(@PathVariable String topic) throws InterruptedException {
        return kafkaService.consume(topic);
    }

    @GetMapping("/users/{id}")
    public EntityModel<User> findUser(@PathVariable int id) {
        User user = userDao.findOne(id)
                .orElseThrow(() -> new UserNotFoundException("id--"+id));
        EntityModel<User> resource = EntityModel.of(user);
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).getAllUsers());
        resource.add(linkTo.withRel("all-users"));
        return resource;
    }

    @DeleteMapping("/users/{id}")
    public void removeUser(@PathVariable int id) {
        boolean removed = userDao.deleteOne(id);
        if(!removed) {
            throw new UserNotFoundException("id--"+id);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<Object> addUser(@RequestBody @Valid User user) {
        Integer id = userDao.createUser(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id).toUri();
        return ResponseEntity.created(location).build();
    }
}
