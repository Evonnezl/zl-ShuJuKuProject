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
}