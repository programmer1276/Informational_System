package com.example.is_curse_work.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private String name;

    private String barcode;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "placed_at", nullable = false)
    private OffsetDateTime placedAt = OffsetDateTime.now();

    @Column(nullable = false)
    private boolean locked = false;

    @Column(nullable = false)
    private String status;

    public Long getProductId() { return productId; }
    public User getOwner() { return owner; }
    public Zone getZone() { return zone; }
    public Category getCategory() { return category; }
    public String getName() { return name; }
    public String getBarcode() { return barcode; }
    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public OffsetDateTime getPlacedAt() { return placedAt; }
    public boolean isLocked() { return locked; }
    public String getStatus() { return status; }
}

