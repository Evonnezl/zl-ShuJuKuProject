package org.example.housebackend.controller;

import org.example.housebackend.entity.HousingRecord;
import org.example.housebackend.service.HousingRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/records")
@CrossOrigin
public class HousingRecordController {

    private final HousingRecordService service;

    public HousingRecordController(HousingRecordService service) {
        this.service = service;
    }

    @GetMapping
    public List<HousingRecord> getAll() {
        return service.getAll();
    }

    @PostMapping
    public void insert(@RequestBody HousingRecord record) {
        service.insert(record);
    }
}