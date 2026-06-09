package org.example.housebackend.controller;

import org.example.housebackend.entity.Application;
import org.example.housebackend.service.ApplicationService;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> insert(@RequestBody Application app) {
        try {
            applicationService.insert(app);
            return ResponseEntity.ok(app);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 分房
    @PutMapping("/{id}/approve")
    public Map<String, Object> approve(@PathVariable Integer id) {
        return applicationService.approve(id);
    }

    // 调房
    @PutMapping("/{id}/transfer")
    public Map<String, Object> transfer(@PathVariable Integer id) {
        return applicationService.transfer(id);
    }

    // 退房
    @PutMapping("/{id}/release")
    public Map<String, Object> release(@PathVariable Integer id) {
        return applicationService.release(id);
    }
}