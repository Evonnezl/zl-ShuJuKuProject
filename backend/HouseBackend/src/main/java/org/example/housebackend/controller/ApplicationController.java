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

    // 获取所有申请
    @GetMapping
    public List<Application> getAll() {
        return applicationService.getAll();
    }

    // 新增申请
    @PostMapping
    public Application insert(@RequestBody Application app) {
        applicationService.insert(app);
        return app;
    }

    // 分房：更新状态为 APPROVED 并分配房屋
    @PutMapping("/{id}/approve")
    public void approve(@PathVariable Integer id) {
        applicationService.approve(id);
    }

    // 调房：更新状态为 APPROVED 并调换房屋
    @PutMapping("/{id}/transfer")
    public void transfer(@PathVariable Integer id) {
        applicationService.transfer(id);
    }

    // 退房：更新状态为 APPROVED 并释放房屋
    @PutMapping("/{id}/release")
    public void release(@PathVariable Integer id) {
        applicationService.release(id);
    }
}