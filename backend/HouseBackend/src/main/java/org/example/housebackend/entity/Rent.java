package org.example.housebackend.entity;

import java.time.LocalDate;

public class Rent {
    private Integer id;
    private Integer userId;
    private Integer houseId;
    private Double rentAmount;    // 月租金 = area × rent_per_m2
    private LocalDate rentDate;   // 租金计算日期

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

    public Double getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(Double rentAmount) {
        this.rentAmount = rentAmount;
    }

    public LocalDate getRentDate() {
        return rentDate;
    }

    public void setRentDate(LocalDate rentDate) {
        this.rentDate = rentDate;
    }
}