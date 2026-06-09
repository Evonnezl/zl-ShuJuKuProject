package org.example.housebackend.entity;

public class HousingStandard {

    private Integer id;
    private Double minArea;
    private Double maxArea;
    private Integer minScore;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Double getMinArea() { return minArea; }
    public void setMinArea(Double minArea) { this.minArea = minArea; }
    public Double getMaxArea() { return maxArea; }
    public void setMaxArea(Double maxArea) { this.maxArea = maxArea; }
    public Integer getMinScore() { return minScore; }
    public void setMinScore(Integer minScore) { this.minScore = minScore; }
}
