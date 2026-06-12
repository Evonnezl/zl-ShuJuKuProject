package org.example.housebackend.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.example.housebackend.util.AesEncryptor;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis TypeHandler：自动对 VARCHAR 字段进行 AES 加密/解密
 * <p>
 * 写入数据库时自动加密，读取时自动解密。业务代码无需感知加密逻辑。
 * <p>
 * 使用方式（在 Mapper XML 中）：
 * <pre>{@code
 * <result column="email" property="email"
 *         typeHandler="org.example.housebackend.typehandler.EncryptedStringTypeHandler"/>
 * }</pre>
 */
@Component
@MappedTypes(String.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class EncryptedStringTypeHandler extends BaseTypeHandler<String> {

    private static AesEncryptor aesEncryptor;

    /**
     * 通过构造器注入 AesEncryptor。
     * 由于 MyBatis 通过反射实例化 TypeHandler，
     * 这里使用静态持有 + Spring 构造器注入的方式。
     */
    public EncryptedStringTypeHandler(AesEncryptor aesEncryptor) {
        EncryptedStringTypeHandler.aesEncryptor = aesEncryptor;
    }

    /**
     * MyBatis 默认无参构造器（反射用），
     * 此时 aesEncryptor 已通过 Spring 构造器注入。
     */
    public EncryptedStringTypeHandler() {
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
            throws SQLException {
        if (aesEncryptor == null) {
            ps.setString(i, parameter);
        } else {
            ps.setString(i, aesEncryptor.encrypt(parameter));
        }
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return decryptIfNeeded(value);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return decryptIfNeeded(value);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return decryptIfNeeded(value);
    }

    private String decryptIfNeeded(String value) {
        if (value == null || value.isEmpty() || aesEncryptor == null) {
            return value;
        }
        return aesEncryptor.decrypt(value);
    }
}
