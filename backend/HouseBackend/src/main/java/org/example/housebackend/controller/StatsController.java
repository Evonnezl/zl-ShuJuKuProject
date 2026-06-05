package org.example.housebackend.controller;

import org.example.housebackend.service.StatsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stats")
@CrossOrigin
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    // 综合统计
    @GetMapping
    public Map<String, Object> getStats() {
        return statsService.getStats();
    }

    // 按面积查询阈值分数
    @GetMapping("/threshold")
    public Map<String, Object> getThresholdByArea(@RequestParam Double area) {
        return statsService.getThresholdByArea(area);
    }

    // 按房号查询单位面积租金
    @GetMapping("/rent")
    public Map<String, Object> getRentByHouseTitle(@RequestParam String title) {
        return statsService.getRentByHouseTitle(title);
    }

    // 房屋租金列表
    @GetMapping("/houses-rent")
    public List<Map<String, Object>> getHouseRentInfo() {
        return statsService.getHouseRentInfo();
    }

    // 住房汇总统计
    @GetMapping("/summary")
    public Map<String, Object> getHousingSummary() {
        return statsService.getHousingSummary();
    }
}