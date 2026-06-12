package org.example.housebackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.housebackend.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {

    List<User> getAllUsers();

    User getById(Integer id);

    void insertUser(User user);

    void updateUser(User user);

    void deleteById(Integer id);

    User findByName(String name);

    // 通过 SHA-256 哈希查找（email 字段已加密，无法直接 WHERE email = ?）
    User findByEmailHash(String hash);

    @Select("SELECT id, name, role, phone, email, password FROM user WHERE id = #{id} LIMIT 1")
    User getFullById(Integer id);
}