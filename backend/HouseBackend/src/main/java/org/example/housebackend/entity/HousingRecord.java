package org.example.housebackend.entity;

import java.time.LocalDate;

public class HousingRecord {

    private Integer id;
    private Integer userId;
    private Integer houseId;
    private Integer score;
    private LocalDate moveInDate;

    // 题目要求住房表包含的冗余字段（分配时快照）
    private String userName;       // 户主
    private String department;     // 部门
    private String titleLevel;     // 职称
    private Integer familySize;    // 家庭人口
    private String houseTitle;     // 房号
    private Double houseArea;      // 住房面积
    private Double rentAmount;     // 月租金

    // ========== getter & setter ==========

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getHouseId() {
        return houseId;
    }

    public void setHouseId(Integer houseId) {
        this.houseId = houseId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public LocalDate getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(LocalDate moveInDate) {
        this.moveInDate = moveInDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTitleLevel() {
        return titleLevel;
    }

    public void setTitleLevel(String titleLevel) {
        this.titleLevel = titleLevel;
    }

    public Integer getFamilySize() {
        return familySize;
    }

    public void setFamilySize(Integer familySize) {
        this.familySize = familySize;
    }

    public String getHouseTitle() {
        return houseTitle;
    }

    public void setHouseTitle(String houseTitle) {
        this.houseTitle = houseTitle;
    }

    public Double getHouseArea() {
        return houseArea;
    }

    public void setHouseArea(Double houseArea) {
        this.houseArea = houseArea;
    }

    public Double getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(Double rentAmount) {
        this.rentAmount = rentAmount;
    }
}