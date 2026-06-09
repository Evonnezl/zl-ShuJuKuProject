package org.example.housebackend.service;

import org.example.housebackend.entity.HousingRecord;
import org.example.housebackend.mapper.HousingRecordMapper;
import org.example.housebackend.mapper.UserMapper;
import org.example.housebackend.mapper.HouseMapper;
import org.example.housebackend.mapper.RentMapper;
import org.example.housebackend.entity.User;
import org.example.housebackend.entity.House;
import org.example.housebackend.entity.Rent;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HousingRecordService {

    private final HousingRecordMapper mapper;
    private final UserMapper userMapper;
    private final HouseMapper houseMapper;
    private final RentMapper rentMapper;

    public HousingRecordService(HousingRecordMapper mapper, UserMapper userMapper,
                                 HouseMapper houseMapper, RentMapper rentMapper) {
        this.mapper = mapper;
        this.userMapper = userMapper;
        this.houseMapper = houseMapper;
        this.rentMapper = rentMapper;
    }

    public List<HousingRecord> getAll() {
        return mapper.getAll();
    }

    public HousingRecord getById(Integer id) {
        return mapper.getById(id);
    }

    public void insert(HousingRecord record) {
        mapper.insert(record);
    }

    public void update(HousingRecord record) {
        mapper.update(record);
    }

    public void deleteById(Integer id) {
        mapper.deleteById(id);
    }

    /**
     * 生成住房分配单，包含住户、房屋、房租完整信息
     */
    public Map<String, Object> getReceipt(Integer id) {
        HousingRecord record = mapper.getById(id);
        if (record == null) return null;

        User user = userMapper.getById(record.getUserId());
        House house = houseMapper.getById(record.getHouseId());

        Map<String, Object> receipt = new HashMap<>();
        receipt.put("recordId", record.getId());
        receipt.put("userName", record.getUserName());
        receipt.put("department", record.getDepartment());
        receipt.put("titleLevel", record.getTitleLevel());
        receipt.put("familySize", record.getFamilySize());
        receipt.put("score", record.getScore());
        receipt.put("houseTitle", record.getHouseTitle());
        receipt.put("houseArea", record.getHouseArea());
        receipt.put("rentAmount", record.getRentAmount());
        receipt.put("moveInDate", record.getMoveInDate() != null ? record.getMoveInDate().toString() : "");

        // 补充信息
        receipt.put("userPhone", user != null ? user.getPhone() : "");
        receipt.put("rentPerM2", house != null ? house.getRentPerM2() : 0);

        return receipt;
    }
}