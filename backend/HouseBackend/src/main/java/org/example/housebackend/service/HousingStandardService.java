package org.example.housebackend.service;

import org.example.housebackend.entity.HousingStandard;
import org.example.housebackend.mapper.HousingStandardMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HousingStandardService {

    private final HousingStandardMapper mapper;

    public HousingStandardService(HousingStandardMapper mapper) {
        this.mapper = mapper;
    }

    public List<HousingStandard> getAll() {
        return mapper.getAll();
    }

    public HousingStandard getById(Integer id) {
        return mapper.getById(id);
    }

    public void insert(HousingStandard standard) {
        mapper.insert(standard);
    }

    public void update(HousingStandard standard) {
        mapper.update(standard);
    }

    public void deleteById(Integer id) {
        mapper.deleteById(id);
    }
}