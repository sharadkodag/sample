package com.sample.service;

import com.sample.entity.User;

import java.util.List;

public interface UserService {


    public void addUser(User user);
    public void updateUser(User user);
    public void deleteUser(User user);
    public User getUserById(Integer id);
    public List<User> getAllUser();

}
