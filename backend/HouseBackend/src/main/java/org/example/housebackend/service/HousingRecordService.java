package org.example.housebackend.service;

import org.example.housebackend.entity.HousingRecord;
import org.example.housebackend.mapper.HousingRecordMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HousingRecordService {

    private final HousingRecordMapper mapper;

    public HousingRecordService(HousingRecordMapper mapper) {
        this.mapper = mapper;
    }

    public List<HousingRecord> getAll() {
        return mapper.getAll();
    }

    public void insert(HousingRecord record) {
        mapper.insert(record);
    }
}