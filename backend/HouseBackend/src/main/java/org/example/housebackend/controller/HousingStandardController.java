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
}