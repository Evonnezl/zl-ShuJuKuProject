package org.example.housebackend.service;

import org.example.housebackend.entity.Application;
import org.example.housebackend.mapper.ApplicationMapper;
import org.example.housebackend.mapper.HouseMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationMapper applicationMapper;
    private final HouseMapper houseMapper;

    public ApplicationService(ApplicationMapper applicationMapper, HouseMapper houseMapper) {
        this.applicationMapper = applicationMapper;
        this.houseMapper = houseMapper;
    }

    public List<Application> getAll() {
        return applicationMapper.getAll();
    }

    // 分房
    public void approve(Integer id) {
        Application app = applicationMapper.getById(id);
        if (app == null) return;

        // 分配空房给申请者
        // 这里简单示例：找到空闲房屋最先分配
        houseMapper.getAll().stream()
                .filter(h -> "空房".equals(h.getStatus()))
                .findFirst()
                .ifPresent(house -> {
                    house.setStatus("已分配");
                    houseMapper.updateHouse(house);

                    app.setHouseId(house.getId());
                    app.setStatus("APPROVED");
                    applicationMapper.update(app);
                });
    }

    // 调房
    public void transfer(Integer id) {
        Application app = applicationMapper.getById(id);
        if (app == null) return;

        // 简单示例：释放原房，分配新房
        if (app.getOriginalHouseId() != null) {
            var oldHouse = houseMapper.getById(app.getOriginalHouseId());
            if (oldHouse != null) {
                oldHouse.setStatus("空房");
                houseMapper.updateHouse(oldHouse);
            }
        }

        // 分配新房
        approve(id);
    }

    // 退房
    public void release(Integer id) {
        Application app = applicationMapper.getById(id);
        if (app == null || app.getHouseId() == null) return;

        var house = houseMapper.getById(app.getHouseId());
        if (house != null) {
            house.setStatus("空房");
            houseMapper.updateHouse(house);
        }

        app.setStatus("APPROVED");
        applicationMapper.update(app);
    }
}