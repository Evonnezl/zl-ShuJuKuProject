package org.example.housebackend.util;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 命令行工具：生成 AES-256 密钥（Base64 编码）
 * <p>
 * 用法：
 * <pre>
 *   java -cp target/classes org.example.housebackend.util.AesKeyGenerator
 * </pre>
 */
public class AesKeyGenerator {

    public static void main(String[] args) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256, new SecureRandom());
        SecretKey key = keyGen.generateKey();
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());

        System.out.println("AES-256 密钥（Base64 编码）:");
        System.out.println(base64Key);
        System.out.println();
        System.out.println("请将此密钥放入 application.properties:");
        System.out.println("app.encryption.aes-key=" + base64Key);
        System.out.println();
        System.out.println("（建议用 Jasypt 加密后再写入配置文件）");
    }
}
