package com.example.is_curse_work.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "fridges")
public class Fridge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fridge_id")
    private Long fridgeId;

    @Column(nullable = false)
    private String name;

    private String location;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "invite_required", nullable = false)
    private boolean inviteRequired = false;

    @Column(name = "owner_id")
    private Long ownerId;

    public Long getFridgeId() { return fridgeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public OffsetDateTime getCreatedAt() { return createdAt; }

    public boolean isInviteRequired() { return inviteRequired; }
    public void setInviteRequired(boolean inviteRequired) { this.inviteRequired = inviteRequired; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
