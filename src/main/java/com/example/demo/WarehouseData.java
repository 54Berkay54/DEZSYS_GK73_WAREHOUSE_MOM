package com.example.demo;

public class WarehouseData {
    private String warehouseId;
    private int quantity;
    private String timestamp;
    // getters + setters
    public WarehouseData() {}

    public WarehouseData(String warehouseId, int quantity, String timestamp) {
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}