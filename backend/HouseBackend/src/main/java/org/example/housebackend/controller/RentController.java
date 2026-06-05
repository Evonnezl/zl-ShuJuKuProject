package org.example.housebackend.controller;

import org.example.housebackend.entity.Rent;
import org.example.housebackend.service.RentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rents")
@CrossOrigin
public class RentController {

    private final RentService rentService;

    public RentController(RentService rentService) {
        this.rentService = rentService;
    }

    @GetMapping
    public List<Rent> getAll() {
        return rentService.getAll();
    }

    @GetMapping("/{id}")
    public Rent getById(@PathVariable Integer id) {
        return rentService.getById(id);
    }

    @PostMapping
    public void insert(@RequestBody Rent rent) {
        rentService.insert(rent);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        rentService.deleteById(id);
    }
}