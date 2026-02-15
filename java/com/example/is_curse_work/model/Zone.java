package com.example.is_curse_work.model;

import jakarta.persistence.*;

@Entity
@Table(name = "zones")
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zone_id")
    private Long zoneId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fridge_id", nullable = false)
    private Fridge fridge;

    @Column(nullable = false)
    private String name;

    @Column(name = "capacity_units", nullable = false)
    private Integer capacityUnits;

    @Column(name = "capacity_volume_l")
    private Double capacityVolumeL;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    public Long getZoneId() { return zoneId; }

    public Fridge getFridge() { return fridge; }
    public void setFridge(Fridge fridge) { this.fridge = fridge; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getCapacityUnits() { return capacityUnits; }
    public void setCapacityUnits(Integer capacityUnits) { this.capacityUnits = capacityUnits; }

    public Double getCapacityVolumeL() { return capacityVolumeL; }
    public void setCapacityVolumeL(Double capacityVolumeL) { this.capacityVolumeL = capacityVolumeL; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}

