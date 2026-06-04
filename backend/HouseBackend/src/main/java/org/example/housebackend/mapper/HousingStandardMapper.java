package org.example.housebackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.housebackend.entity.HousingStandard;

import java.util.List;

@Mapper
public interface HousingStandardMapper {

    List<HousingStandard> getAll();
}