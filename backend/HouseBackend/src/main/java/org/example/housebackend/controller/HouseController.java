package org.example.housebackend.controller;

import org.example.housebackend.entity.House;
import org.example.housebackend.service.HouseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/houses")
@CrossOrigin
public class HouseController {

    private final HouseService houseService;

    public HouseController(HouseService houseService) {
        this.houseService = houseService;
    }

    @GetMapping
    public List<House> list() {
        return houseService.getAll();
    }

    @GetMapping("/{id}")
    public House getById(@PathVariable Integer id) {
        return houseService.getById(id);
    }

    @PostMapping
    public void insert(@RequestBody House house) {
        houseService.insert(house);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Integer id, @RequestBody House house) {
        house.setId(id);
        houseService.update(house);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        houseService.deleteById(id);
    }
}