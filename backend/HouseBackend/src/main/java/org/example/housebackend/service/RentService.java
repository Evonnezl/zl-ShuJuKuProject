package org.example.housebackend.service;

import org.example.housebackend.entity.Rent;
import org.example.housebackend.mapper.RentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentService {

    private final RentMapper rentMapper;

    public RentService(RentMapper rentMapper) {
        this.rentMapper = rentMapper;
    }

    public List<Rent> getAll() {
        return rentMapper.getAll();
    }

    public Rent getById(Integer id) {
        return rentMapper.getById(id);
    }

    public void insert(Rent rent) {
        rentMapper.insert(rent);
    }

    public void deleteByHouseId(Integer houseId) {
        rentMapper.deleteByHouseId(houseId);
    }

    public void deleteById(Integer id) {
        rentMapper.deleteById(id);
    }
}