package com.example.is_curse_work.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String name;

    private String room;

    @Column(name = "notif_email_on", nullable = false)
    private boolean notifEmailOn = true;

    @Column(name = "notif_push_on", nullable = false)
    private boolean notifPushOn = true;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "blocked_at")
    private OffsetDateTime blockedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public Long getUserId() { return userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public boolean isNotifEmailOn() { return notifEmailOn; }
    public void setNotifEmailOn(boolean notifEmailOn) { this.notifEmailOn = notifEmailOn; }

    public boolean isNotifPushOn() { return notifPushOn; }
    public void setNotifPushOn(boolean notifPushOn) { this.notifPushOn = notifPushOn; }

    public OffsetDateTime getCreatedAt() { return createdAt; }

    public OffsetDateTime getBlockedAt() { return blockedAt; }
    public void setBlockedAt(OffsetDateTime blockedAt) { this.blockedAt = blockedAt; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
}
