package org.example.housebackend.service;

import org.example.housebackend.entity.User;
import org.example.housebackend.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserMapper userMapper;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public List<User> getAllUsers() {
        return userMapper.getAllUsers();
    }

    public User getById(Integer id) {
        return userMapper.getById(id);
    }

    public void insertUser(User user) {
        userMapper.insertUser(user);
    }

    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }
}