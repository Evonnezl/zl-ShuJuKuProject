package org.example.housebackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.housebackend.entity.HousingRecord;

import java.util.List;

@Mapper
public interface HousingRecordMapper {

    List<HousingRecord> getAll();

    void insert(HousingRecord record);
}