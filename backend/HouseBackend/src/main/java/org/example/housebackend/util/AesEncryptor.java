package org.example.housebackend.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256-GCM 对称加密工具
 * <p>
 * 每次加密使用随机 12 字节 IV，输出格式为 Base64(IV + ciphertext)。
 * 解密时从密文中提取 IV 再进行解密。
 * <p>
 * 密钥通过 {@code app.encryption.aes-key} 配置（Base64 编码的 256-bit 密钥），
 * 该配置值本身由 Jasypt 加密保护。
 */
@Component
public class AesEncryptor {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;   // 96-bit IV (NIST recommended)
    private static final int GCM_TAG_LENGTH = 128;  // 128-bit authentication tag
    private static final int AES_KEY_LENGTH = 32;   // 256-bit key

    private final SecretKeySpec keySpec;
    private final SecureRandom secureRandom = new SecureRandom();

    public AesEncryptor(@Value("${app.encryption.aes-key}") String base64Key) {
        if (base64Key == null || base64Key.isBlank()) {
            throw new IllegalArgumentException(
                    "AES 加密密钥未配置！请在 application.properties 中设置 app.encryption.aes-key");
        }
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        if (keyBytes.length != AES_KEY_LENGTH) {
            throw new IllegalArgumentException(
                    "AES 密钥长度必须为 32 字节（256-bit），当前为 " + keyBytes.length + " 字节。" +
                    "请使用 openssl rand -base64 32 生成");
        }
        this.keySpec = new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * 加密明文，返回 Base64 编码的密文（含 IV）
     *
     * @param plaintext 明文（null 或空字符串原样返回）
     * @return Base64(IV + ciphertext)，或原值（如为 null/空）
     */
    public String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isEmpty()) {
            return plaintext;
        }
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            // IV + ciphertext
            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("AES 加密失败", e);
        }
    }

    /**
     * 解密密文，返回明文
     *
     * @param encrypted Base64 编码的密文（null 或空字符串原样返回）
     * @return 明文，或原值（如为 null/空）
     */
    public String decrypt(String encrypted) {
        if (encrypted == null || encrypted.isEmpty()) {
            return encrypted;
        }
        try {
            byte[] combined = Base64.getDecoder().decode(encrypted);

            // 如果解密出的内容太短（不是加密数据），可能是已解密的明文，直接返回
            if (combined.length < GCM_IV_LENGTH) {
                return encrypted;
            }

            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] ciphertext = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(combined, GCM_IV_LENGTH, ciphertext, 0, ciphertext.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            // 解密失败：可能是明文数据（迁移前），原样返回
            return encrypted;
        }
    }
}
