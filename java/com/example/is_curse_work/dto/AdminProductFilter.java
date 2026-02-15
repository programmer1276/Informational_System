package com.example.is_curse_work.dto;

public class AdminProductFilter {
    private String ownerEmail;
    private Long fridgeId;
    private String status;

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
    public Long getFridgeId() { return fridgeId; }
    public void setFridgeId(Long fridgeId) { this.fridgeId = fridgeId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
