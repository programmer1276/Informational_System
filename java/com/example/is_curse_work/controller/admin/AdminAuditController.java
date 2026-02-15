package com.example.is_curse_work.controller.admin;

import com.example.is_curse_work.repository.AuditLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/audit")
public class AdminAuditController {

    private final AuditLogRepository auditLogs;

    public AdminAuditController(AuditLogRepository auditLogs) {
        this.auditLogs = auditLogs;
    }

    @GetMapping
    public String list(@RequestParam(name = "actor", required = false) String actor,
                       @RequestParam(name = "action", required = false) String action,
                       Model model) {
        model.addAttribute("logs", auditLogs.search(blankToNull(actor), blankToNull(action), PageRequest.of(0, 100)));
        model.addAttribute("actor", actor);
        model.addAttribute("action", action);
        return "admin/audit";
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
