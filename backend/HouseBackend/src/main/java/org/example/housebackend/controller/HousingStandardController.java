package org.example.housebackend.controller;

import org.example.housebackend.entity.HousingStandard;
import org.example.housebackend.service.HousingStandardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/standards")
@CrossOrigin
public class HousingStandardController {

    private final HousingStandardService service;

    public HousingStandardController(HousingStandardService service) {
        this.service = service;
    }

    @GetMapping
    public List<HousingStandard> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public HousingStandard getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public void insert(@RequestBody HousingStandard standard) {
        service.insert(standard);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Integer id, @RequestBody HousingStandard standard) {
        standard.setId(id);
        service.update(standard);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.deleteById(id);
    }
}