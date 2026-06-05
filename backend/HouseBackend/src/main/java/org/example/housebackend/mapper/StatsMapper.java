package org.example.housebackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;
import java.util.List;

@Mapper
public interface StatsMapper {

    int countEmptyHouses();

    int countAllocatedHouses();

    int countApplications();

    Integer getMinThresholdScore();

    // 按面积查询阈值分数
    Integer getThresholdByArea(@Param("area") Double area);

    // 按房号查询单位面积租金
    Double getRentPerM2ByHouseTitle(@Param("title") String title);

    // 所有房屋的租金信息
    List<Map<String, Object>> getHouseRentInfo();

    // 住房统计信息
    List<Map<String, Object>> getHousingSummary();
}