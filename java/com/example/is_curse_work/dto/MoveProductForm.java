package com.example.is_curse_work.dto;

import jakarta.validation.constraints.NotNull;

public class MoveProductForm {
    @NotNull
    private Long toZoneId;

    private String comment;

    public Long getToZoneId() { return toZoneId; }
    public void setToZoneId(Long toZoneId) { this.toZoneId = toZoneId; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}

