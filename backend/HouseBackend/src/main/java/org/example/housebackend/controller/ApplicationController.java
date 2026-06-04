package org.example.housebackend.controller;

import org.example.housebackend.entity.Application;
import org.example.housebackend.service.ApplicationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
@CrossOrigin
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    // 查询所有申请
    @GetMapping
    public List<Application> list() {
        return applicationService.getAll();
    }

    // 添加申请
    @PostMapping
    public void add(@RequestBody Application application) {
        applicationService.insert(application);
    }

    // 更新申请状态
    @PutMapping
    public void update(@RequestBody Application application) {
        applicationService.updateStatus(application);
    }
}