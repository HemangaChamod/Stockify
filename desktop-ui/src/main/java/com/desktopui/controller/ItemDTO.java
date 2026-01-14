package com.desktopui.controller;

public class ItemDTO {

    private Long id;
    private String name;
    private String description;
    private double sellingPrice;
    private double costPrice;
    private String unit;
    private int stockInHand;
    private double totalStockValue;
    private String imageUrl;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getSellingPrice() { return sellingPrice; }
    public double getCostPrice() { return costPrice; }
    public String getUnit() { return unit; }
    public int getStockInHand() { return stockInHand; }
    public double getTotalStockValue() { return totalStockValue; }
    public String getImageUrl() { return imageUrl; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setSellingPrice(double sellingPrice) { this.sellingPrice = sellingPrice; }
    public void setCostPrice(double costPrice) { this.costPrice = costPrice; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setStockInHand(int stockInHand) { this.stockInHand = stockInHand; }
    public void setTotalStockValue(double totalStockValue) { this.totalStockValue = totalStockValue; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
