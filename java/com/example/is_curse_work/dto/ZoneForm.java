package com.example.is_curse_work.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ZoneForm {
    @NotBlank
    private String name;

    @NotNull
    private Integer capacityUnits;

    private Double capacityVolumeL;

    @NotNull
    private Integer sortOrder;

    private boolean active = true;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getCapacityUnits() { return capacityUnits; }
    public void setCapacityUnits(Integer capacityUnits) { this.capacityUnits = capacityUnits; }
    public Double getCapacityVolumeL() { return capacityVolumeL; }
    public void setCapacityVolumeL(Double capacityVolumeL) { this.capacityVolumeL = capacityVolumeL; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
