package pram.ms.userservice.dao;

import pram.ms.userservice.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    List<User> getAll();
    Optional<User> findOne(Integer id);
    boolean deleteOne(Integer id);
    Integer createUser(User user);
}
