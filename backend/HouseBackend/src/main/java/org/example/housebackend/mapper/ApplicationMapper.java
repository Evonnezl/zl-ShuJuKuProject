package org.example.housebackend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.housebackend.entity.Application;

import java.util.List;

@Mapper
public interface ApplicationMapper {
    List<Application> getAll();
    void insert(Application application);
    void updateStatus(Application application);
    Application getById(Integer id);
    void update(Application application);
}