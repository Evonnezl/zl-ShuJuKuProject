package org.example.housebackend.config;

import org.example.housebackend.util.AesEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * 启动时一次性数据迁移：将已有明文的 email/phone 加密并计算哈希
 * <p>
 * 迁移策略：
 * <ol>
 *   <li>查询 email_hash IS NULL 的用户</li>
 *   <li>对 email 和 phone 使用 AES-256-GCM 加密</li>
 *   <li>计算 email 和 phone 的 SHA-256 哈希</li>
 *   <li>UPDATE 回数据库</li>
 * </ol>
 * 幂等：第二次启动时已全部加密完毕，自动跳过。
 */
@Component
@Order(1)
public class DataMigrationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataMigrationRunner.class);

    private final JdbcTemplate jdbcTemplate;
    private final AesEncryptor aesEncryptor;

    public DataMigrationRunner(DataSource dataSource, AesEncryptor aesEncryptor) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.aesEncryptor = aesEncryptor;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("=== 开始检查敏感数据迁移 ===");

        // 查询需要迁移的行（email 不为空但 email_hash 为空 = 未加密的明文数据）
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, email, phone FROM user WHERE email IS NOT NULL AND email != '' AND email_hash IS NULL");

        if (rows.isEmpty()) {
            log.info("无需迁移：所有用户数据已加密");
            return;
        }

        log.info("发现 {} 条未加密的用户数据，开始迁移...", rows.size());

        int migrated = 0;
        for (Map<String, Object> row : rows) {
            try {
                Integer id = (Integer) row.get("id");
                String email = (String) row.get("email");
                String phone = (String) row.get("phone");

                // 加密
                String encryptedEmail = aesEncryptor.encrypt(email);
                String encryptedPhone = aesEncryptor.encrypt(phone);

                // 计算哈希（邮箱统一 trim + lowercase）
                String emailHash = email != null ? sha256(email.trim().toLowerCase()) : null;
                String phoneHash = phone != null && !phone.isEmpty() ? sha256(phone) : null;

                // 直接 JDBC 更新（绕过 MyBatis TypeHandler，避免二次加密）
                jdbcTemplate.update(
                        "UPDATE user SET email = ?, phone = ?, email_hash = ?, phone_hash = ? WHERE id = ?",
                        encryptedEmail, encryptedPhone, emailHash, phoneHash, id);

                migrated++;
            } catch (Exception e) {
                log.error("迁移用户 id={} 失败: {}", row.get("id"), e.getMessage());
            }
        }

        log.info("=== 敏感数据迁移完成：{}/{} 条 ===", migrated, rows.size());
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
