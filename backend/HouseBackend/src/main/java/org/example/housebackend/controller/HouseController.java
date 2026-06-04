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

    // 查询所有房屋
    @GetMapping
    public List<House> list() {
        return houseService.getAll();
    }

    // 分房/退房（更新状态）
    @PutMapping
    public void update(@RequestBody House house) {
        houseService.update(house);
    }
}