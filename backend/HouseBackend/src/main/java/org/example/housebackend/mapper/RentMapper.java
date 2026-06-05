package org.example.housebackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.housebackend.entity.Rent;

import java.util.List;

@Mapper
public interface RentMapper {

    List<Rent> getAll();

    Rent getById(Integer id);

    void insert(Rent rent);

    void deleteByHouseId(Integer houseId);

    void deleteById(Integer id);
}