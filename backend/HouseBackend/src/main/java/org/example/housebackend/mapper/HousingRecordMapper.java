package org.example.housebackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.housebackend.entity.HousingRecord;

import java.util.List;

@Mapper
public interface HousingRecordMapper {

    List<HousingRecord> getAll();

    HousingRecord getById(Integer id);

    void insert(HousingRecord record);

    void update(HousingRecord record);

    void deleteById(Integer id);
}