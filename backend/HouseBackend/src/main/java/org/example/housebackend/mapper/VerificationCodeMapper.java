package org.example.housebackend.mapper;

import org.apache.ibatis.annotations.*;
import org.example.housebackend.entity.VerificationCode;

@Mapper
public interface VerificationCodeMapper {

    @Insert("INSERT INTO verification_code(email, code, expires_at) VALUES(#{email}, #{code}, #{expiresAt})")
    void insert(VerificationCode vc);

    @Select("SELECT * FROM verification_code WHERE email=#{email} AND used=0 ORDER BY id DESC LIMIT 1")
    VerificationCode findLatestByEmail(String email);

    @Update("UPDATE verification_code SET used=1 WHERE id=#{id}")
    void markUsed(Integer id);

    @Delete("DELETE FROM verification_code WHERE expires_at < NOW()")
    void deleteExpired();
}
