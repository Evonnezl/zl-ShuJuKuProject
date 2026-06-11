package org.example.housebackend.controller;

import org.example.housebackend.entity.User;
import org.example.housebackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    @PostMapping
    public void insertUser(@RequestBody User user) {
        userService.insertUser(user);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable Integer id, @RequestBody User user) {
        user.setId(id);
        userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteById(id);
    }

    /**
     * 个人信息编辑（含密码修改验证）
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        User user = userService.getById(id);
        if (user == null) {
            Map<String, String> err = new HashMap<>();
            err.put("message", "用户不存在");
            return ResponseEntity.badRequest().body(err);
        }

        // 如果要修改密码，验证旧密码
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (newPassword != null && !newPassword.isEmpty()) {
            if (oldPassword == null || oldPassword.isEmpty()) {
                Map<String, String> err = new HashMap<>();
                err.put("message", "请输入旧密码");
                return ResponseEntity.badRequest().body(err);
            }
            // 获取完整用户信息（含密码）进行验证
            User fullUser = userService.getFullById(id);
            if (!userService.verifyPassword(oldPassword, fullUser.getPassword())) {
                Map<String, String> err = new HashMap<>();
                err.put("message", "旧密码错误");
                return ResponseEntity.badRequest().body(err);
            }
            user.setPassword(newPassword);
        }

        if (body.containsKey("name")) user.setName(body.get("name"));
        if (body.containsKey("email")) user.setEmail(body.get("email"));
        if (body.containsKey("phone")) user.setPhone(body.get("phone"));

        userService.updateUser(user);

        Map<String, String> result = new HashMap<>();
        result.put("message", "个人信息更新成功");
        return ResponseEntity.ok(result);
    }
}