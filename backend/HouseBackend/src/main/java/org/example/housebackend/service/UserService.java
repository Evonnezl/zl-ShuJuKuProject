package org.example.housebackend.service;

import org.example.housebackend.entity.User;
import org.example.housebackend.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        // 计算哈希（用于加密后的精确查找）
        computeHashes(user);
        userMapper.insertUser(user);
    }

    public void updateUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        // 如果 email/phone 有变化，重新计算哈希
        computeHashes(user);
        userMapper.updateUser(user);
    }

    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    /**
     * 根据邮箱查找用户（通过 SHA-256 哈希查找，因为 email 列已加密）
     */
    public User findByEmail(String email) {
        if (email == null || email.isEmpty()) return null;
        return userMapper.findByEmailHash(sha256(email.trim().toLowerCase()));
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
        User user = findByEmail(email);
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

    // ========== 私有方法 ==========

    /**
     * 计算 email 和 phone 的 SHA-256 哈希（用于加密后的精确查找）
     * 邮箱统一 trim + lowercase 以保证一致性
     */
    private void computeHashes(User user) {
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            user.setEmailHash(sha256(user.getEmail().trim().toLowerCase()));
        }
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            user.setPhoneHash(sha256(user.getPhone()));
        }
    }

    private static String sha256(String input) {
        if (input == null || input.isEmpty()) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
