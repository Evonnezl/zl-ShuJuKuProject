package org.example.housebackend.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Jasypt 配置解密处理器
 * <p>
 * 在 Spring 加载配置后、Bean 初始化前，将 {@code ENC(...)} 格式的密文解密。
 * 主密码从环境变量 {@code JASYPT_ENCRYPTOR_PASSWORD} 获取。
 * <p>
 * 如果不设置该环境变量，则跳过解密（明文模式，兼容开发环境）。
 * <p>
 * 注册方式：META-INF/spring.factories
 */
public class JasyptEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String ENC_PREFIX = "ENC(";
    private static final String ENC_SUFFIX = ")";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String password = System.getenv("JASYPT_ENCRYPTOR_PASSWORD");
        if (password == null || password.isEmpty()) {
            // 未设置环境变量，明文模式
            return;
        }

        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(password);
        // 使用 Jasypt 默认算法（PBEWithMD5AndDES），兼容所有 JDK 版本

        MutablePropertySources sources = environment.getPropertySources();
        List<PropertySource<?>> toWrap = new ArrayList<>();

        for (PropertySource<?> ps : sources) {
            toWrap.add(ps);
        }

        for (PropertySource<?> ps : toWrap) {
            sources.replace(ps.getName(), new DecryptingPropertySource(ps, encryptor));
        }
    }

    /**
     * 包装原始 PropertySource，在 getProperty() 时自动解密 ENC(...) 值
     */
    private static class DecryptingPropertySource extends PropertySource<PropertySource<?>> {

        private final StandardPBEStringEncryptor encryptor;

        DecryptingPropertySource(PropertySource<?> source, StandardPBEStringEncryptor encryptor) {
            super(source.getName(), source);
            this.encryptor = encryptor;
        }

        @Override
        public Object getProperty(String name) {
            Object value = ((PropertySource<?>) this.source).getProperty(name);
            if (value instanceof String str && str.startsWith(ENC_PREFIX) && str.endsWith(ENC_SUFFIX)) {
                String encrypted = str.substring(ENC_PREFIX.length(), str.length() - ENC_SUFFIX.length());
                try {
                    return encryptor.decrypt(encrypted);
                } catch (Exception e) {
                    // 解密失败返回原值（可能是非加密值）
                    return str;
                }
            }
            return value;
        }
    }
}
