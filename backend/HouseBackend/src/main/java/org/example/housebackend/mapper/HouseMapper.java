package org.example.housebackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.housebackend.entity.House;

import java.util.List;

@Mapper
public interface HouseMapper {

    List<House> getAll();
    void updateHouse(House house);
    House getById(Integer id);

    @org.apache.ibatis.annotations.Insert("INSERT INTO house(title, area, rent_per_m2, status) VALUES(#{title}, #{area}, #{rentPerM2}, #{status})")
    @org.apache.ibatis.annotations.Options(useGeneratedKeys = true, keyProperty = "id")
    void insertHouse(House house);

    @org.apache.ibatis.annotations.Delete("DELETE FROM house WHERE id = #{id}")
    void deleteById(Integer id);
}