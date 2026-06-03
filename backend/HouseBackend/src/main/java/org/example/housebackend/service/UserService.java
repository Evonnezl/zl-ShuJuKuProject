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
}