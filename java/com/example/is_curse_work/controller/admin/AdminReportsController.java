package com.example.is_curse_work.controller.admin;

import com.example.is_curse_work.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/reports")
public class AdminReportsController {

    private final ReportService reports;

    public AdminReportsController(ReportService reports) {
        this.reports = reports;
    }

    @GetMapping
    public String index(Model model) {
        var summary = reports.buildSummary();
        if (summary == null) {
            summary = new com.example.is_curse_work.dto.ReportSummary(0, 0, java.util.List.of());
        }
        model.addAttribute("summary", summary);
        return "admin/reports";
    }
}
