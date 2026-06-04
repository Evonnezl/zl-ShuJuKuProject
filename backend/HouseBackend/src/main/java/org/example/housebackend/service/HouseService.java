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

    // 获取所有房屋
    public List<House> getAll() {
        return houseMapper.getAll();
    }

    // 更新房屋状态
    public void update(House house) {
        houseMapper.updateHouse(house);
    }
}