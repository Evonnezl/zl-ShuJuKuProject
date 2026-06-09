package org.example.housebackend.controller;

import org.example.housebackend.entity.User;
import org.example.housebackend.entity.VerificationCode;
import org.example.housebackend.mapper.VerificationCodeMapper;
import org.example.housebackend.service.EmailService;
import org.example.housebackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@RestController
@CrossOrigin
public class AuthController {

    private final UserService userService;
    private final VerificationCodeMapper vcMapper;
    private final EmailService emailService;

    public AuthController(UserService userService, VerificationCodeMapper vcMapper,
                          EmailService emailService) {
        this.userService = userService;
        this.vcMapper = vcMapper;
        this.emailService = emailService;
    }

    /**
     * 登录（支持邮箱或姓名）
     */
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String account = body.get("account");
        String password = body.get("password");
        if (account == null || password == null || account.isEmpty() || password.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "账号和密码不能为空"));
        }

        // 先按邮箱查，再按姓名查
        User user = userService.loginByEmail(account, password);
        if (user == null) user = userService.login(account, password);

        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("message", "账号或密码错误"));
        }
        return ResponseEntity.ok(user);
    }

    /**
     * 发送邮箱验证码（5分钟有效）
     */
    @PostMapping("/auth/send-code")
    public ResponseEntity<?> sendCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "邮箱不能为空"));
        }

        // 检查邮箱是否已注册
        User exist = userService.findByEmail(email);
        if (exist != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "该邮箱已被注册"));
        }

        // 清理过期验证码
        vcMapper.deleteExpired();

        // 生成6位随机验证码
        String code = String.format("%06d", new Random().nextInt(999999));

        // 存入数据库
        VerificationCode vc = new VerificationCode();
        vc.setEmail(email);
        vc.setCode(code);
        vc.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        vcMapper.insert(vc);

        emailService.sendVerificationCode(email, code);

        return ResponseEntity.ok(Map.of(
                "message", "验证码已发送至 " + email + "，请查收邮件（有效期5分钟）"
        ));
    }

    /**
     * 注册新用户
     */
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String code = body.get("code");
        String name = body.get("name");
        String password = body.get("password");

        if (email == null || code == null || name == null || password == null
                || email.isEmpty() || code.isEmpty() || name.isEmpty() || password.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "请填写所有字段"));
        }
        if (password.length() < 4) {
            return ResponseEntity.badRequest().body(Map.of("message", "密码至少4位"));
        }

        // 校验验证码
        VerificationCode vc = vcMapper.findLatestByEmail(email);
        if (vc == null || !vc.getCode().equals(code)) {
            return ResponseEntity.badRequest().body(Map.of("message", "验证码错误"));
        }
        if (vc.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("message", "验证码已过期"));
        }

        // 标记验证码已使用
        vcMapper.markUsed(vc.getId());

        // 检查邮箱/姓名是否已存在
        if (userService.findByEmail(email) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "该邮箱已被注册"));
        }

        // 创建用户
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("tenant");
        user.setPhone("");
        userService.insertUser(user);

        user.setPassword(null);
        return ResponseEntity.ok(Map.of("message", "注册成功", "user", user));
    }
}
