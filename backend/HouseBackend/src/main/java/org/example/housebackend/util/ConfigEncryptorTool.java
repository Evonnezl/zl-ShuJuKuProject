package org.example.housebackend.util;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

/**
 * 命令行工具：加密配置值，生成 ENC(...) 格式密文
 * <p>
 * 用法：
 * <pre>
 *   java -cp target/classes org.example.housebackend.util.ConfigEncryptorTool "your-jasypt-password" "plaintext-to-encrypt"
 * </pre>
 * 或设置 JASYPT_ENCRYPTOR_PASSWORD 环境变量后省略第一个参数：
 * <pre>
 *   java -cp target/classes org.example.housebackend.util.ConfigEncryptorTool "plaintext-to-encrypt"
 * </pre>
 */
public class ConfigEncryptorTool {

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String password;
        String plaintext;

        if (args.length >= 2) {
            password = args[0];
            plaintext = args[1];
        } else {
            password = System.getenv("JASYPT_ENCRYPTOR_PASSWORD");
            plaintext = args[0];
            if (password == null || password.isEmpty()) {
                System.err.println("错误：未提供 Jasypt 主密码。");
                System.err.println("请设置 JASYPT_ENCRYPTOR_PASSWORD 环境变量，或作为第一个参数传入。");
                return;
            }
        }

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        encryptor.setAlgorithm("PBEWithHmacSHA512AndAES_256");

        String encrypted = encryptor.encrypt(plaintext);
        System.out.println("明文: " + plaintext);
        System.out.println("密文: ENC(" + encrypted + ")");
        System.out.println();
        System.out.println("请将以下内容放入配置文件:");
        System.out.println("your.property=ENC(" + encrypted + ")");
    }

    private static void printUsage() {
        System.out.println("用法:");
        System.out.println("  java -cp target/classes org.example.housebackend.util.ConfigEncryptorTool <jasypt-password> <plaintext>");
        System.out.println("  java -cp target/classes org.example.housebackend.util.ConfigEncryptorTool <plaintext>   (需设置 JASYPT_ENCRYPTOR_PASSWORD 环境变量)");
        System.out.println();
        System.out.println("示例:");
        System.out.println("  java -cp target/classes org.example.housebackend.util.ConfigEncryptorTool mySecret your-password");
    }
}
