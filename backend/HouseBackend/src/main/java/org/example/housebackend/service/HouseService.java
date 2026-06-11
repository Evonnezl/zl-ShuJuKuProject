package org.example.housebackend.service;

import org.example.housebackend.entity.House;
import org.example.housebackend.mapper.HouseMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HouseService {

    private final HouseMapper houseMapper;

    public HouseService(HouseMapper houseMapper) {
        this.houseMapper = houseMapper;
    }

    public List<House> getAll() {
        return houseMapper.getAll();
    }

    public House getById(Integer id) {
        return houseMapper.getById(id);
    }

    public void update(House house) {
        houseMapper.updateHouse(house);
    }

    public void insert(House house) {
        houseMapper.insertHouse(house);
    }

    public void deleteById(Integer id) {
        houseMapper.deleteById(id);
    }
}