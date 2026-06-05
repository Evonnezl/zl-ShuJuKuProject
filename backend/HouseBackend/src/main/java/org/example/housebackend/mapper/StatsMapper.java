package org.example.housebackend.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StatsMapper {

    int countEmptyHouses();

    int countAllocatedHouses();

    int countApplications();

    Integer getMinThresholdScore();
}