package com.example.is_curse_work.controller.admin;

import com.example.is_curse_work.model.Role;
import com.example.is_curse_work.repository.RoleRepository;
import com.example.is_curse_work.repository.UserRepository;
import com.example.is_curse_work.security.CustomUserDetails;
import com.example.is_curse_work.service.AuditService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@Controller
@RequestMapping("/admin/users")
public class AdminUsersController {

    private final UserRepository users;
    private final RoleRepository roles;
    private final AuditService audit;

    public AdminUsersController(UserRepository users, RoleRepository roles, AuditService audit) {
        this.users = users;
        this.roles = roles;
        this.audit = audit;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", users.findAllWithRoles());
        return "admin/users";
    }

    @PostMapping("/{id}/block")
    public String block(@AuthenticationPrincipal CustomUserDetails actor,
                        @PathVariable("id") Long id,
                        RedirectAttributes redirectAttributes) {
        var user = users.findById(id).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Пользователь не найден");
            return "redirect:/admin/users";
        }
        user.setBlockedAt(OffsetDateTime.now());
        users.save(user);
        audit.log(actor.getUsername(), "BLOCK_USER", "User", id.toString(), "Blocked user");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/unblock")
    public String unblock(@AuthenticationPrincipal CustomUserDetails actor,
                          @PathVariable("id") Long id,
                          RedirectAttributes redirectAttributes) {
        var user = users.findById(id).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Пользователь не найден");
            return "redirect:/admin/users";
        }
        user.setBlockedAt(null);
        users.save(user);
        audit.log(actor.getUsername(), "UNBLOCK_USER", "User", id.toString(), "Unblocked user");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/roles/{code}/grant")
    public String grantRole(@AuthenticationPrincipal CustomUserDetails actor,
                            @PathVariable("id") Long id,
                            @PathVariable("code") String code,
                            RedirectAttributes redirectAttributes) {
        var user = users.findById(id).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Пользователь не найден");
            return "redirect:/admin/users";
        }
        Role role = roles.findByCode(code.toUpperCase());
        if (role == null) {
            redirectAttributes.addFlashAttribute("error", "Роль не найдена");
            return "redirect:/admin/users";
        }
        user.getRoles().add(role);
        users.save(user);
        audit.log(actor.getUsername(), "GRANT_ROLE", "User", id.toString(), "Granted " + code);
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/roles/{code}/revoke")
    public String revokeRole(@AuthenticationPrincipal CustomUserDetails actor,
                             @PathVariable("id") Long id,
                             @PathVariable("code") String code,
                             RedirectAttributes redirectAttributes) {
        var user = users.findById(id).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Пользователь не найден");
            return "redirect:/admin/users";
        }
        Role role = roles.findByCode(code.toUpperCase());
        if (role == null) {
            redirectAttributes.addFlashAttribute("error", "Роль не найдена");
            return "redirect:/admin/users";
        }
        user.getRoles().remove(role);
        users.save(user);
        audit.log(actor.getUsername(), "REVOKE_ROLE", "User", id.toString(), "Revoked " + code);
        return "redirect:/admin/users";
    }
}
