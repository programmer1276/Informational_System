package com.example.is_curse_work.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterForm {
    @Email @NotBlank
    private String email;

    @NotBlank
    private String name;

    private String room;

    @NotBlank @Size(min = 6)
    private String password;

    private boolean notifEmailOn = true;
    private boolean notifPushOn = true;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isNotifEmailOn() { return notifEmailOn; }
    public void setNotifEmailOn(boolean notifEmailOn) { this.notifEmailOn = notifEmailOn; }
    public boolean isNotifPushOn() { return notifPushOn; }
    public void setNotifPushOn(boolean notifPushOn) { this.notifPushOn = notifPushOn; }
}

