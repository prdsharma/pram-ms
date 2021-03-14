package pram.ms.userservice.dao;

import org.springframework.stereotype.Repository;
import pram.ms.userservice.model.User;

import java.util.*;

@Repository
public class UserCollectionDao implements UserDao{

    private static final List<User> users = new ArrayList<>();

    private static int count = 3;
    
    static {
        users.add(new User(1, "James", new Date()));
        users.add(new User(2, "Roberts", new Date()));
        users.add(new User(3, "Thomas", new Date()));
    }
    

    @Override
    public List<User> getAll() {
        return users;
    }

    @Override
    public Optional<User> findOne(Integer id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public boolean deleteOne(Integer id) {
        return users.removeIf(user -> user.getId() == id);
    }

    @Override
    public Integer createUser(User user) {
        if(user.getId() == null) {
            user.setId(++count);
        }
        users.add(user);
        return user.getId();
    }

}
