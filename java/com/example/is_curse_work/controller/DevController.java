package com.example.is_curse_work.controller;

import com.example.is_curse_work.service.NotificationService;
import com.example.is_curse_work.repository.RoleRepository;
import com.example.is_curse_work.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import com.example.is_curse_work.service.EmailService;
import com.example.is_curse_work.model.User;

@RestController
@RequestMapping("/dev")
@Profile("dev")
public class DevController {

    private final NotificationService notificationService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public DevController(NotificationService notificationService, RoleRepository roleRepository, UserRepository userRepository, EmailService emailService) {
        this.notificationService = notificationService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @PostMapping("/run-expiry")
    public ResponseEntity<?> runExpiry(@RequestParam(name = "fridgeId", defaultValue = "1") Long fridgeId,
                                       @RequestParam(name = "days", defaultValue = "3") int days) {
        int created = notificationService.runExpiryBatch(fridgeId, days);
        return ResponseEntity.ok(Map.of("created", created));
    }

    @PostMapping("/grant-role")
    public ResponseEntity<?> grantRole(@RequestParam String email, @RequestParam String role) {
        var roleEnt = roleRepository.findByCode(role);
        if (roleEnt == null) return ResponseEntity.badRequest().body(Map.of("error", "role not found"));
        var opt = userRepository.findByEmailFetchRoles(email);
        if (opt.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "user not found"));
        var u = opt.get();
        u.getRoles().add(roleEnt);
        userRepository.save(u);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/send-test-email")
    public ResponseEntity<?> sendTestEmail(@RequestParam String email, @RequestParam(name = "name", defaultValue = "Test User") String name) {
        User u = new User();
        u.setEmail(email);
        u.setName(name);
        // best-effort async send
        try {
            emailService.sendRegistrationEmail(u);
        } catch (Exception ignored) {}
        return ResponseEntity.ok(Map.of("ok", true));
    }
}
