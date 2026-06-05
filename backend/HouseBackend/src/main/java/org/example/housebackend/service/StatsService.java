package org.example.housebackend.service;

import org.example.housebackend.mapper.StatsMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
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
        map.put("houseRentInfo", statsMapper.getHouseRentInfo());
        map.put("housingSummary", statsMapper.getHousingSummary());
        return map;
    }

    // 按面积查询阈值分数
    public Map<String, Object> getThresholdByArea(Double area) {
        Map<String, Object> map = new HashMap<>();
        map.put("area", area);
        map.put("minScore", statsMapper.getThresholdByArea(area));
        return map;
    }

    // 按房号查询单位面积租金
    public Map<String, Object> getRentByHouseTitle(String title) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("rentPerM2", statsMapper.getRentPerM2ByHouseTitle(title));
        return map;
    }

    // 房屋租金列表
    public List<Map<String, Object>> getHouseRentInfo() {
        return statsMapper.getHouseRentInfo();
    }

    // 住房汇总统计
    public Map<String, Object> getHousingSummary() {
        return statsMapper.getHousingSummary().isEmpty()
                ? new HashMap<>()
                : statsMapper.getHousingSummary().get(0);
    }
}