package com.example.is_curse_work.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminFridgeForm {
    @NotBlank
    private String name;
    private String location;
    private boolean inviteRequired;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public boolean isInviteRequired() { return inviteRequired; }
    public void setInviteRequired(boolean inviteRequired) { this.inviteRequired = inviteRequired; }
}
