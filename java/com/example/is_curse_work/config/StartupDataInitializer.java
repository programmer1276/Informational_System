package com.example.is_curse_work.config;

import com.example.is_curse_work.model.Role;
import com.example.is_curse_work.model.User;
import com.example.is_curse_work.repository.RoleRepository;
import com.example.is_curse_work.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;

@Component
public class StartupDataInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(StartupDataInitializer.class);
    private final RoleRepository roles;
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public StartupDataInitializer(RoleRepository roles, UserRepository users, PasswordEncoder passwordEncoder) {
        this.roles = roles;
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        // Ensure roles exist
        ensureRole("ADMIN", "Administrator");
        ensureRole("MODERATOR", "Moderator");
        ensureRole("USER", "User");

        // Ensure test users exist with expected roles (password = "password")
        ensureUserWithRole("alice@example.com", "Alice", "101", "ADMIN");
        ensureUserWithRole("vlad@gmail.com", "Vlad", "201", "ADMIN");
        ensureUserWithRole("bob@example.com", "Bob", "102", "MODERATOR", "USER");
    }

    private void ensureRole(String code, String name) {
        var r = roles.findByCode(code);
        if (r == null) {
            Role role = new Role();
            role.setCode(code);
            role.setName(name);
            roles.save(role);
            log.info("Created role {}", code);
        }
    }

    private void ensureUserWithRole(String email, String name, String room, String... roleCodes) {
        var opt = users.findByEmailFetchRoles(email);
        if (opt.isPresent()) {
            // ensure roles present
            User u = opt.get();
            for (String rc: roleCodes) {
                var r = roles.findByCode(rc);
                if (r != null && u.getRoles().stream().noneMatch(rr -> rr.getCode().equals(rc))) {
                    u.getRoles().add(r);
                }
            }
            users.save(u);
            log.info("Ensured existing user {} with roles {}", email, u.getRoles().stream().map(Role::getCode).toList());
            return;
        }

        User u = new User();
        u.setEmail(email);
        u.setName(name);
        u.setRoom(room);
        u.setPasswordHash(passwordEncoder.encode("password"));
        u.setNotifEmailOn(true);
        u.setNotifPushOn(true);
        var set = new HashSet<Role>();
        for (String rc: roleCodes) {
            var r = roles.findByCode(rc);
            if (r != null) set.add(r);
        }
        u.setRoles(set);
        users.save(u);
        log.info("Created user {} with roles {}", email, set.stream().map(Role::getCode).toList());
    }
}
