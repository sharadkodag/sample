package com.sample.wrapper;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.sample.entity.User;
import com.sample.module.ExecutorConfig;
import com.sample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserWrapper {

    @Autowired
    UserService userService;
    @Autowired
    ExecutorConfig executorConfig;

//    @Resource
    ExecutorService executorService;

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

    @PostConstruct
    private void initializeCacheOnAppStartup() throws ExecutionException {
//        ExecutorService executorService = Executors.newFixedThreadPool(2);
//        Future<List<Integer>> userIds = executorService.submit(() -> userService.getAllUser().stream().map(User::getId).collect(Collectors.toList()));
//        try {
//            userCache.getAll(userIds.get());
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

//        Future<List<Integer>> userIds = executorService.submit(() -> userService.getAllUser().stream().map(User::getId).collect(Collectors.toList()));
//        try {
//            userCache.getAll(userIds.get());
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("ExecutorService");
                List<Integer> collect = userService.getAllUser().stream().map(User::getId).collect(Collectors.toList());
                try {
                    userCache.getAll(collect);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<10; i++) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Executor service 2");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("ExecutorService 3");

            }
        });

        executorService.shutdown();

//        executorConfig.taskExecutor().execute(() -> {
//            List<Integer> collect = userService.getAllUser().stream().map(User::getId).collect(Collectors.toList());
//            try {
//                userCache.getAll(collect);
//            } catch (ExecutionException e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        executorConfig.taskExecutor().execute(() -> {
//            for(int i=0; i<100; i++){
//                //                    Thread.sleep(1000);
//            }
//        });
//
//        executorConfig.taskExecutor().execute(() -> {
//            for(int i=0; i<100; i++){
//                //                    .sleep(Thread1000);
//                System.out.println("thread 1");
//            }
//        });
//
//        executorConfig.taskExecutor().execute(() -> {
//            for(int i=0; i<100; i++){
//
//                System.out.println("thread 2");
//            }
//        });
//
//        executorConfig.taskExecutor().execute(() -> {
//            for(int i=0; i<100; i++){
//                System.out.println("thread 3");
//            }
//        });

        System.out.println("Hi");

//        List<Integer> collect = userService.getAllUser().stream().map(User::getId).collect(Collectors.toList());
//        userCache.getAll(collect);

//        getAllUser().forEach(user -> userCache.put(user.getId(), user));

    }

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

    public Collection<User> getAllUserDataFromCache(){
        return userCache.asMap().values();
    }

//    public List<User> getAllUser() {
//        System.err.println("From Database");
//        return userService.getAllUser();
//    }

}
