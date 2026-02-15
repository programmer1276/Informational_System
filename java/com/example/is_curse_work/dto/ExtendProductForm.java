package com.example.is_curse_work.dto;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public class ExtendProductForm {
    @NotNull
    private OffsetDateTime newExpiresAt;

    private String comment;

    public OffsetDateTime getNewExpiresAt() { return newExpiresAt; }
    public void setNewExpiresAt(OffsetDateTime newExpiresAt) { this.newExpiresAt = newExpiresAt; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}

