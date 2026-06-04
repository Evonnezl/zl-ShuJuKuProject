package org.example.housebackend.service;

import org.example.housebackend.entity.Application;
import org.example.housebackend.mapper.ApplicationMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationMapper applicationMapper;

    // 构造器注入
    public ApplicationService(ApplicationMapper applicationMapper) {
        this.applicationMapper = applicationMapper;
    }

    public List<Application> getAll() {
        return applicationMapper.getAll();
    }

    public void insert(Application application) {
        applicationMapper.insert(application);
    }

    public void updateStatus(Application application) {
        applicationMapper.updateStatus(application);
    }
}