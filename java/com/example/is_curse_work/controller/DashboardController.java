package com.example.is_curse_work.controller;

import com.example.is_curse_work.dto.UserProductDto;
import com.example.is_curse_work.security.CustomUserDetails;
import com.example.is_curse_work.service.FridgeService;
import com.example.is_curse_work.service.ProductService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.OffsetDateTime;
import java.util.List;

@Controller
public class DashboardController {

    private final ProductService productService;
    private final FridgeService fridgeService;

    public DashboardController(ProductService productService, FridgeService fridgeService) {
        this.productService = productService;
        this.fridgeService = fridgeService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        var myProducts = productService.getMyProducts(me.getUserId());
        var fridges = fridgeService.listFridges();
        var now = OffsetDateTime.now();
        var soon = now.plusDays(3);
        List<UserProductDto> expiringSoon = myProducts.stream()
                .filter(p -> p.expiresAt() != null)
                .filter(p -> !p.expiresAt().isAfter(soon))
                .filter(p -> p.expiresAt().isAfter(now))
                .filter(p -> !"EXPIRED".equalsIgnoreCase(p.status()))
                .toList();

        model.addAttribute("myProducts", myProducts);
        model.addAttribute("fridges", fridges);
        model.addAttribute("productCount", myProducts.size());
        model.addAttribute("fridgeCount", fridges.size());
        model.addAttribute("expiringSoon", expiringSoon);
        return "dashboard";
    }
}
