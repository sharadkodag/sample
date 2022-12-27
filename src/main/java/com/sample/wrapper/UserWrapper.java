package com.sample.wrapper;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sample.entity.User;
import com.sample.service.UserService;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserWrapper {

    @Autowired
    UserService userService;

    @PostConstruct
    private void initializeCacheOnAppStartup() throws ExecutionException {
        List<Integer> userIds = userService.getAllUser().stream().map(User::getId).collect(Collectors.toList());
        userCache.getAll(userIds);
//        getAllUser().forEach(user -> userCache.put(user.getId(), user));
    }

    private LoadingCache<Integer, User> userCache = CacheBuilder.newBuilder().build(
            new CacheLoader<Integer, User>() {
                @Override
                public User load(Integer key) throws Exception {
                    System.err.print("From database");
                    return (User) userService.getUserById(key);
                }

                @Override
                public Map<Integer, User> loadAll(Iterable<? extends Integer> keys) throws Exception {
                    return userService.getAllUser().stream().collect(Collectors.toMap(User::getId, Function.identity()));
                }
            }
    );

    private LoadingCache<Integer, Set<User>> allUseCache = CacheBuilder.newBuilder().build(
            new CacheLoader<Integer, Set<User>>() {
                @Override
                public Set<User> load(Integer key) throws Exception {
                    System.err.print("From database");
                    return (Set<User>) userService.getAllUser();
                }
            }
    );

    public void addUser(User user) {
        userService.addUser(user);
        userCache.put(user.getId(), user);
//        userCache.put(user.getId(), user);
    }

    public void updateUser(User user) {
        userCache.invalidate(user);
        userCache.put(user.getId(), user);
        userService.updateUser(user);
    }

    public void deleteUser(User user) {
        userCache.invalidate(user);
        userService.deleteUser(user);
    }

    public User getUserById(Integer id) {
        try {
            return userCache.get(id);
        } catch (ExecutionException e) {

            System.err.println("Exception");
            throw new RuntimeException(e);
        }
    }

    public Collection<User> getAllUserDataFromChache(){
        return userCache.asMap().values();
    }

//    public List<User> getAllUser() {
//        System.err.println("From Database");
//        return userService.getAllUser();
//    }

}
