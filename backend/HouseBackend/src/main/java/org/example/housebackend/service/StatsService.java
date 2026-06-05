package org.example.housebackend.service;

import org.example.housebackend.mapper.StatsMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatsService {

    private final StatsMapper statsMapper;

    public StatsService(StatsMapper statsMapper) {
        this.statsMapper = statsMapper;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> map = new HashMap<>();

        map.put("emptyHouse", statsMapper.countEmptyHouses());
        map.put("allocatedHouse", statsMapper.countAllocatedHouses());
        map.put("applicationCount", statsMapper.countApplications());
        map.put("thresholdScore", statsMapper.getMinThresholdScore());

        return map;
    }
}