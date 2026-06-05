package org.example.housebackend.entity;

import java.time.LocalDateTime;

public class Application {
    private Integer id;
    private Integer userId;
    private Integer houseId;
    private String type;        // ALLOCATE / TRANSFER / RETURN
    private String status;      // 申请状态
    private String department;
    private String titleLevel;
    private Integer familySize;
    private Integer score;
    private Double requestArea;
    private Integer originalHouseId;
    private Double originalHouseArea;
    private LocalDateTime createTime;

    // getter & setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getHouseId() { return houseId; }
    public void setHouseId(Integer houseId) { this.houseId = houseId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getTitleLevel() { return titleLevel; }
    public void setTitleLevel(String titleLevel) { this.titleLevel = titleLevel; }

    public Integer getFamilySize() { return familySize; }
    public void setFamilySize(Integer familySize) { this.familySize = familySize; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Double getRequestArea() { return requestArea; }
    public void setRequestArea(Double requestArea) { this.requestArea = requestArea; }

    public Integer getOriginalHouseId() { return originalHouseId; }
    public void setOriginalHouseId(Integer originalHouseId) { this.originalHouseId = originalHouseId; }

    public Double getOriginalHouseArea() { return originalHouseArea; }
    public void setOriginalHouseArea(Double originalHouseArea) { this.originalHouseArea = originalHouseArea; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}