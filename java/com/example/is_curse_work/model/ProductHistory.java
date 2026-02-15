package com.example.is_curse_work.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "product_history")
public class ProductHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "from_zone_id")
    private Long fromZoneId;

    @Column(name = "to_zone_id")
    private Long toZoneId;

    @Column(name = "old_expires_at")
    private OffsetDateTime oldExpiresAt;

    @Column(name = "new_expires_at")
    private OffsetDateTime newExpiresAt;

    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Long getHistoryId() { return historyId; }
    public Product getProduct() { return product; }
    public String getEventType() { return eventType; }
    public Long getFromZoneId() { return fromZoneId; }
    public Long getToZoneId() { return toZoneId; }
    public OffsetDateTime getOldExpiresAt() { return oldExpiresAt; }
    public OffsetDateTime getNewExpiresAt() { return newExpiresAt; }
    public String getComment() { return comment; }
    public User getActor() { return actor; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}

