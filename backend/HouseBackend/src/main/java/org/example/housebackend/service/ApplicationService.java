package org.example.housebackend.service;

import org.example.housebackend.entity.*;
import org.example.housebackend.mapper.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 加入分房队列（不再立即分房，符合题目要求的"插入队列"机制）
     */
    public Map<String, Object> queueForAllocation(Integer id) {
        Map<String, Object> result = new HashMap<>();
        Application app = applicationMapper.getById(id);
        if (app == null || !"PENDING".equals(app.getStatus())) {
            result.put("success", false);
            result.put("message", "申请不存在或状态不是待审批");
            return result;
        }

        // 计算分数并插入队列
        int calculatedScore = calculateScore(app);
        app.setScore(calculatedScore);
        app.setStatus("QUEUED");
        applicationMapper.update(app);

        // 查看队列排名
        List<Application> queue = getQueueSorted();
        int position = 0;
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).getId().equals(id)) { position = i + 1; break; }
        }

        result.put("success", true);
        result.put("message", "已加入分房队列（得分" + calculatedScore + "，队列排名第" + position + "位，共" + queue.size() + "人排队）");
        return result;
    }

    /**
     * 批量分房（题目要求：月末统一处理）
     * 按分数从高到低依次处理队列中的所有申请
     */
    public Map<String, Object> batchAllocate() {
        Map<String, Object> result = new HashMap<>();
        List<Application> queue = getQueueSorted();
        if (queue.isEmpty()) {
            result.put("success", false);
            result.put("message", "分房队列为空");
            return result;
        }

        int allocated = 0;
        int rejected = 0;
        int waiting = 0;
        StringBuilder detail = new StringBuilder();

        for (Application app : queue) {
            Map<String, Object> single = doAllocate(app);
            if (Boolean.TRUE.equals(single.get("success"))) {
                allocated++;
                detail.append("✅ ").append(single.get("message")).append("\n");
            } else if (single.containsKey("rejected") && Boolean.TRUE.equals(single.get("rejected"))) {
                rejected++;
                detail.append("❌ ").append(single.get("message")).append("\n");
            } else {
                waiting++;
                detail.append("⏳ ").append(single.get("message")).append("\n");
            }
        }

        result.put("success", true);
        result.put("message", "分房完成：成功" + allocated + "人，拒绝" + rejected + "人，等待" + waiting + "人");
        result.put("detail", detail.toString().trim());
        result.put("allocated", allocated);
        result.put("rejected", rejected);
        result.put("waiting", waiting);
        return result;
    }

    /**
     * 单条分房逻辑（从队列中取出的申请进行实际分配）
     */
    private Map<String, Object> doAllocate(Application app) {
        Map<String, Object> result = new HashMap<>();
        int calculatedScore = app.getScore() != null ? app.getScore() : calculateScore(app);

        HousingStandard matchedStandard = findStandard(app.getRequestArea());
        if (matchedStandard == null) {
            app.setStatus("REJECTED");
            applicationMapper.update(app);
            result.put("success", false);
            result.put("rejected", true);
            result.put("message", app.getUserId() + " 未找到匹配住房标准，已拒绝");
            return result;
        }
        if (calculatedScore < matchedStandard.getMinScore()) {
            app.setStatus("REJECTED");
            applicationMapper.update(app);
            result.put("success", false);
            result.put("rejected", true);
            result.put("message", app.getUserId() + " 分数不足（" + calculatedScore + "＜" + matchedStandard.getMinScore() + "），已拒绝");
            return result;
        }

        List<House> gradeHouses = findHousesInGrade(matchedStandard);
        if (gradeHouses.isEmpty()) {
            result.put("success", false);
            result.put("message", app.getUserId() + " 该等级暂无空房，留在队列等待");
            return result;
        }

        House bestHouse = gradeHouses.get(0);
        User user = userMapper.getById(app.getUserId());

        HousingRecord record = new HousingRecord();
        record.setUserId(app.getUserId());
        record.setHouseId(bestHouse.getId());
        record.setScore(calculatedScore);
        record.setMoveInDate(LocalDate.now());
        record.setUserName(user != null ? user.getName() : "");
        record.setDepartment(app.getDepartment());
        record.setTitleLevel(app.getTitleLevel());
        record.setFamilySize(app.getFamilySize());
        record.setHouseTitle(bestHouse.getTitle());
        record.setHouseArea(bestHouse.getArea());

        double rentAmount = bestHouse.getArea() * bestHouse.getRentPerM2();
        record.setRentAmount(rentAmount);
        housingRecordMapper.insert(record);

        bestHouse.setStatus("已分配");
        houseMapper.updateHouse(bestHouse);

        Rent rent = new Rent();
        rent.setUserId(app.getUserId());
        rent.setHouseId(bestHouse.getId());
        rent.setRentAmount(rentAmount);
        rent.setRentDate(LocalDate.now());
        rentMapper.insert(rent);

        app.setHouseId(bestHouse.getId());
        app.setStatus("APPROVED");
        applicationMapper.update(app);

        result.put("success", true);
        result.put("message", (user != null ? user.getName() : app.getUserId()) + " → " + bestHouse.getTitle()
                + "（" + bestHouse.getArea() + "㎡，" + String.format("%.0f", rentAmount) + "元/月）");
        return result;
    }

    /**
     * 获取队列中所有申请，按分数从高到低排序
     */
    private List<Application> getQueueSorted() {
        return applicationMapper.getAll().stream()
                .filter(a -> "QUEUED".equals(a.getStatus()))
                .sorted(Comparator.comparingInt(Application::getScore).reversed())
                .toList();
    }

    // ==================== 调房 ====================

    /**
     * 调房流程：
     * 1. 根据申请者条件确定住房等级
     * 2. 在空房中查找属于该等级的房屋
     * 3. 释放原住房
     * 4. 分配新房（复用分房逻辑）
     */
    public Map<String, Object> transfer(Integer id) {
        Application app = applicationMapper.getById(id);
        if (app == null || !"PENDING".equals(app.getStatus())) {
            Map<String, Object> r = new HashMap<>();
            r.put("success", false); r.put("message", "申请不存在或状态异常"); return r;
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
        return doAllocate(app);
    }

    // ==================== 退房 ====================

    /**
     * 退房流程：
     * 1. 删除住房记录
     * 2. 删除房租记录
     * 3. 房屋标记为空房
     * 4. 更新申请状态
     */
    public Map<String, Object> release(Integer id) {
        Application app = applicationMapper.getById(id);
        if (app == null || !"PENDING".equals(app.getStatus())) {
            Map<String, Object> r = new HashMap<>();
            r.put("success", false); r.put("message", "申请不存在或状态异常"); return r;
        }

        Integer houseId = app.getHouseId();
        if (houseId == null) {
            Map<String, Object> r = new HashMap<>();
            r.put("success", false); r.put("message", "未关联房屋"); return r;
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

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "退房成功，房屋 " + house.getTitle() + " 已释放");
        return result;
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
            if (dept.equals("计算机学院") || dept.equals("电子信息学院")) {
                score += 30;
            } else if (dept.equals("数学与统计学院") || dept.equals("物理学院")
                    || dept.equals("化学学院") || dept.equals("生命科学学院")) {
                score += 25;
            } else if (dept.equals("经济管理学院") || dept.equals("法学院")
                    || dept.equals("外国语学院") || dept.equals("马克思主义学院")) {
                score += 20;
            } else {
                score += 15;
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
     * 查找属于指定住房标准等级的空房，按面积从大到小排列
     */
    private List<House> findHousesInGrade(HousingStandard standard) {
        final double lo = standard.getMinArea();
        final double hi = standard.getMaxArea();

        return houseMapper.getAll().stream()
                .filter(h -> "空房".equals(h.getStatus()) || "empty".equals(h.getStatus()))
                .filter(h -> h.getArea() > lo && h.getArea() <= hi)
                .sorted(Comparator.comparingDouble(House::getArea).reversed())
                .toList();
    }

    /**
     * 查找对应面积的住房标准（申请面积落在哪个等级范围内）
     */
    private HousingStandard findStandard(Double requestArea) {
        if (requestArea == null) return null;

        List<HousingStandard> standards = housingStandardMapper.getAll();
        standards.sort(Comparator.comparingDouble(HousingStandard::getMaxArea));

        for (HousingStandard std : standards) {
            if (requestArea > std.getMinArea() && requestArea <= std.getMaxArea()) {
                return std;
            }
        }

        // 申请面积超出所有标准，取最大
        return standards.isEmpty() ? null : standards.get(standards.size() - 1);
    }
}