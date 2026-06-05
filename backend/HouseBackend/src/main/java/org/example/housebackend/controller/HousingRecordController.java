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

    @GetMapping("/{id}")
    public HousingRecord getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public void insert(@RequestBody HousingRecord record) {
        service.insert(record);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Integer id, @RequestBody HousingRecord record) {
        record.setId(id);
        service.update(record);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.deleteById(id);
    }
}