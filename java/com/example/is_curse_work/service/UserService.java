package com.example.is_curse_work.service;

import com.example.is_curse_work.dto.RegisterForm;
import com.example.is_curse_work.model.User;

public interface UserService {
    User register(RegisterForm form);
}

