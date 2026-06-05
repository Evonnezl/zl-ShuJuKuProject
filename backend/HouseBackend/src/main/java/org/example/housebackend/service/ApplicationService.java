package org.example.housebackend.service;

import org.example.housebackend.entity.*;
import org.example.housebackend.mapper.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationMapper applicationMapper;
    private final HouseMapper houseMapper;
    private final HousingRecordMapper housingRecordMapper;
    private final HousingStandardMapper housingStandardMapper;
    private final RentMapper rentMapper;
    private final UserMapper userMapper;

    public ApplicationService(ApplicationMapper applicationMapper,
                              HouseMapper houseMapper,
                              HousingRecordMapper housingRecordMapper,
                              HousingStandardMapper housingStandardMapper,
                              RentMapper rentMapper,
                              UserMapper userMapper) {
        this.applicationMapper = applicationMapper;
        this.houseMapper = houseMapper;
        this.housingRecordMapper = housingRecordMapper;
        this.housingStandardMapper = housingStandardMapper;
        this.rentMapper = rentMapper;
        this.userMapper = userMapper;
    }

    // ==================== 查询 ====================

    public List<Application> getAll() {
        return applicationMapper.getAll();
    }

    public Application getById(Integer id) {
        return applicationMapper.getById(id);
    }

    /**
     * 新增申请，含业务校验：
     * - 分房：已有住房的用户不能申请
     * - 调房/退房：没有住房的用户不能申请
     */
    public void insert(Application app) {
        if (app.getStatus() == null) {
            app.setStatus("PENDING");
        }

        // 查该用户是否已有住房
        boolean hasHouse = housingRecordMapper.getAll().stream()
                .anyMatch(r -> r.getUserId().equals(app.getUserId()));

        if ("ALLOCATE".equals(app.getType())) {
            if (hasHouse) {
                throw new RuntimeException("该用户已有住房，不能申请分房");
            }
        }

        if ("TRANSFER".equals(app.getType()) || "RETURN".equals(app.getType())) {
            if (!hasHouse) {
                throw new RuntimeException("该用户没有住房，不能申请调房或退房");
            }
        }

        // 调房：自动补全原住房面积
        if ("TRANSFER".equals(app.getType()) && app.getOriginalHouseId() != null) {
            House oldHouse = houseMapper.getById(app.getOriginalHouseId());
            if (oldHouse != null) {
                app.setOriginalHouseArea(oldHouse.getArea());
            }
        }

        applicationMapper.insert(app);
    }

    // ==================== 分房（核心） ====================

    /**
     * 分房流程（对应题目要求）：
     * 1. 校验申请合法性
     * 2. 根据部门+职称+家庭人数计算分数
     * 3. 分数 ≥ 对应面积阈值 → 查找空房
     * 4. 好房优先分配（面积大的优先）
     * 5. 创建住房记录（含冗余快照字段）
     * 6. 更新房屋状态为"已分配"
     * 7. 计算房租写入 rent 表
     * 8. 更新申请状态为 APPROVED
     */
    public void approve(Integer id) {
        // 1. 获取申请
        Application app = applicationMapper.getById(id);
        if (app == null || !"PENDING".equals(app.getStatus())) {
            return;
        }

        // 2. 计算分数（题目：根据申请者情况计算其分数）
        int calculatedScore = calculateScore(app);
        app.setScore(calculatedScore);

        // 3. 阈值校验：查找对应面积的住房标准
        HousingStandard matchedStandard = findStandard(app.getRequestArea());
        if (matchedStandard == null) {
            // 没有匹配的标准，拒绝
            app.setStatus("REJECTED");
            applicationMapper.update(app);
            return;
        }
        if (calculatedScore < matchedStandard.getMinScore()) {
            // 分数不够，拒绝
            app.setStatus("REJECTED");
            applicationMapper.update(app);
            return;
        }

        // 4. 查找空房，好房优先（按面积从大到小排列）
        List<House> emptyHouses = houseMapper.getAll().stream()
                .filter(h -> "空房".equals(h.getStatus()))
                .sorted(Comparator.comparingDouble(House::getArea).reversed())
                .toList();

        if (emptyHouses.isEmpty()) {
            // 没有空房，申请保持 PENDING 等待
            return;
        }

        // 分配最优空房（面积最大的）
        House bestHouse = emptyHouses.get(0);

        // 5. 创建住房记录（题目要求：户主、部门、职称、家庭人口、住房分数、房号、住房面积）
        User user = userMapper.getById(app.getUserId());

        HousingRecord record = new HousingRecord();
        record.setUserId(app.getUserId());
        record.setHouseId(bestHouse.getId());
        record.setScore(calculatedScore);
        record.setMoveInDate(LocalDate.now());

        // 冗余快照字段（题目要求的住房表内容）
        record.setUserName(user != null ? user.getName() : "");
        record.setDepartment(app.getDepartment());
        record.setTitleLevel(app.getTitleLevel());
        record.setFamilySize(app.getFamilySize());
        record.setHouseTitle(bestHouse.getTitle());
        record.setHouseArea(bestHouse.getArea());

        // 6. 计算房租 = 面积 × 每平米月租金
        double rentAmount = bestHouse.getArea() * bestHouse.getRentPerM2();
        record.setRentAmount(rentAmount);

        housingRecordMapper.insert(record);

        // 7. 更新房屋状态
        bestHouse.setStatus("已分配");
        houseMapper.updateHouse(bestHouse);

        // 8. 写入房租表（题目要求：计算房租并将算出的房租写到房租文件中）
        Rent rent = new Rent();
        rent.setUserId(app.getUserId());
        rent.setHouseId(bestHouse.getId());
        rent.setRentAmount(rentAmount);
        rent.setRentDate(LocalDate.now());
        rentMapper.insert(rent);

        // 9. 更新申请状态
        app.setHouseId(bestHouse.getId());
        app.setStatus("APPROVED");
        applicationMapper.update(app);
    }

    // ==================== 调房 ====================

    /**
     * 调房流程：
     * 1. 根据申请者条件确定住房等级
     * 2. 在空房中查找属于该等级的房屋
     * 3. 释放原住房
     * 4. 分配新房（复用分房逻辑）
     */
    public void transfer(Integer id) {
        Application app = applicationMapper.getById(id);
        if (app == null || !"PENDING".equals(app.getStatus())) {
            return;
        }

        // 释放原住房
        if (app.getOriginalHouseId() != null) {
            House oldHouse = houseMapper.getById(app.getOriginalHouseId());
            if (oldHouse != null) {
                oldHouse.setStatus("空房");
                houseMapper.updateHouse(oldHouse);
            }

            // 删除原住房记录和房租记录
            List<HousingRecord> records = housingRecordMapper.getAll();
            records.stream()
                    .filter(r -> r.getUserId().equals(app.getUserId())
                            && r.getHouseId().equals(app.getOriginalHouseId()))
                    .findFirst()
                    .ifPresent(r -> {
                        housingRecordMapper.deleteById(r.getId());
                        rentMapper.deleteByHouseId(app.getOriginalHouseId());
                    });
        }

        // 分配新房（调用分房逻辑）
        approve(id);
    }

    // ==================== 退房 ====================

    /**
     * 退房流程：
     * 1. 删除住房记录
     * 2. 删除房租记录
     * 3. 房屋标记为空房
     * 4. 更新申请状态
     */
    public void release(Integer id) {
        Application app = applicationMapper.getById(id);
        if (app == null || !"PENDING".equals(app.getStatus())) {
            return;
        }

        Integer houseId = app.getHouseId();
        if (houseId == null) {
            return;
        }

        // 删除住房记录
        List<HousingRecord> records = housingRecordMapper.getAll();
        records.stream()
                .filter(r -> r.getUserId().equals(app.getUserId())
                        && r.getHouseId().equals(houseId))
                .findFirst()
                .ifPresent(r -> housingRecordMapper.deleteById(r.getId()));

        // 删除房租记录
        rentMapper.deleteByHouseId(houseId);

        // 房屋标记为空房
        House house = houseMapper.getById(houseId);
        if (house != null) {
            house.setStatus("空房");
            houseMapper.updateHouse(house);
        }

        // 更新申请状态
        app.setStatus("APPROVED");
        applicationMapper.update(app);
    }

    // ==================== 辅助方法 ====================

    /**
     * 根据申请者情况计算分数
     * 公式：部门基础分 + 职称分 + 家庭人数 × 5
     */
    private int calculateScore(Application app) {
        int score = 0;

        // 部门基础分
        if (app.getDepartment() != null) {
            String dept = app.getDepartment();
            if (dept.contains("计算机") || dept.contains("重点")) {
                score += 30;
            } else if (dept.contains("学院") || dept.contains("系")) {
                score += 20;
            } else {
                score += 10;
            }
        }

        // 职称分
        if (app.getTitleLevel() != null) {
            String title = app.getTitleLevel();
            if (title.contains("教授")) {
                score += 50;
            } else if (title.contains("副教授")) {
                score += 40;
            } else if (title.contains("讲师")) {
                score += 30;
            } else if (title.contains("助教")) {
                score += 20;
            } else {
                score += 10;
            }
        }

        // 家庭人数（每人 5 分）
        if (app.getFamilySize() != null) {
            score += app.getFamilySize() * 5;
        }

        return score;
    }

    /**
     * 查找对应面积的住房标准（取面积最接近且 ≥ 申请面积的档位）
     */
    private HousingStandard findStandard(Double requestArea) {
        if (requestArea == null) return null;

        List<HousingStandard> standards = housingStandardMapper.getAll();
        // 按面积升序排列
        standards.sort(Comparator.comparingDouble(HousingStandard::getArea));

        // 找到第一个面积 ≥ 申请面积的档位
        for (HousingStandard std : standards) {
            if (std.getArea() >= requestArea) {
                return std;
            }
        }

        // 如果申请面积超过所有标准，取最大标准
        return standards.isEmpty() ? null : standards.get(standards.size() - 1);
    }
}