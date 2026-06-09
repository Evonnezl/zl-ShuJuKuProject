package org.example.housebackend.mapper;

import org.apache.ibatis.annotations.Mapper;
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

    @org.apache.ibatis.annotations.Select("SELECT id, name, role, phone, email, password FROM user WHERE email = #{email} LIMIT 1")
    User findByEmail(String email);
}