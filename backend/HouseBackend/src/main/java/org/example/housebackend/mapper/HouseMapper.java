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
}