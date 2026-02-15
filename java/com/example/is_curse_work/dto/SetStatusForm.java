package com.example.is_curse_work.dto;

import jakarta.validation.constraints.NotBlank;

public class SetStatusForm {
    @NotBlank
    private String status; // EATEN|TAKEN|DISPOSED|EXPIRED|ACTIVE

    private String comment;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
