package org.example.housebackend.service;

import org.example.housebackend.entity.User;
import org.example.housebackend.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userMapper.insertUser(user);
    }

    public void updateUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userMapper.updateUser(user);
    }

    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    /**
     * 根据邮箱查找用户
     */
    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    /**
     * 登录验证：根据姓名查找用户并校验密码
     */
    public User login(String name, String password) {
        User user = userMapper.findByName(name);
        if (user == null || user.getPassword() == null) return null;
        if (passwordEncoder.matches(password, user.getPassword())) {
            user.setPassword(null);
            return user;
        }
        return null;
    }

    /**
     * 登录验证：根据邮箱查找用户并校验密码
     */
    public User loginByEmail(String email, String password) {
        User user = userMapper.findByEmail(email);
        if (user == null || user.getPassword() == null) return null;
        if (passwordEncoder.matches(password, user.getPassword())) {
            user.setPassword(null);
            return user;
        }
        return null;
    }

    /**
     * 获取完整用户信息（含密码）
     */
    public User getFullById(Integer id) {
        return userMapper.getFullById(id);
    }

    /**
     * 验证密码
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
