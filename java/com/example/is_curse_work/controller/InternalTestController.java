package com.example.is_curse_work.controller;

import com.example.is_curse_work.model.User;
import com.example.is_curse_work.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Temporary internal endpoint for testing email sending via curl.
 * Accessible without auth (per SecurityConfig) — remove or restrict in production.
 */
@RestController
@RequestMapping("/internal")
public class InternalTestController {

    private final EmailService emailService;

    public InternalTestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-test-email")
    public ResponseEntity<?> sendTestEmail(@RequestParam String email,
                                           @RequestParam(name = "name", defaultValue = "Tester") String name) {
        User u = new User();
        u.setEmail(email);
        u.setName(name);
        try {
            emailService.sendRegistrationEmail(u);
        } catch (Exception ignored) {}
        return ResponseEntity.ok(Map.of("ok", true, "email", email));
    }
}
