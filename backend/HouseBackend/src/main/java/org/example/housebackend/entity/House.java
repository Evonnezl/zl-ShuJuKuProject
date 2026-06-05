package org.example.housebackend.entity;

public class House {
    private Integer id;
    private String title;
    private String status;
    private Double area;
    private Double rentPerM2;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getArea() { return area; }
    public void setArea(Double area) { this.area = area; }

    public Double getRentPerM2() { return rentPerM2; }
    public void setRentPerM2(Double rentPerM2) { this.rentPerM2 = rentPerM2; }
}
