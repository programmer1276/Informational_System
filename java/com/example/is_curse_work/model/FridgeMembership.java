package com.example.is_curse_work.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name = "fridge_memberships")
@IdClass(FridgeMembership.PK.class)
public class FridgeMembership {

    @Id
    @Column(name = "fridge_id")
    private Long fridgeId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "is_moderator", nullable = false)
    private boolean moderator = false;

    @Column(name = "joined_at", nullable = false)
    private OffsetDateTime joinedAt = OffsetDateTime.now();

    @Column(name = "left_at")
    private OffsetDateTime leftAt;

    public Long getFridgeId() { return fridgeId; }
    public Long getUserId() { return userId; }
    public boolean isModerator() { return moderator; }
    public OffsetDateTime getJoinedAt() { return joinedAt; }
    public OffsetDateTime getLeftAt() { return leftAt; }

    public static class PK implements Serializable {
        public Long fridgeId;
        public Long userId;
    }
}

