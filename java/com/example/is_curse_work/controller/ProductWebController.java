package com.example.is_curse_work.controller;

import com.example.is_curse_work.dto.*;
import com.example.is_curse_work.repository.CategoryRepository;
import com.example.is_curse_work.repository.FridgeMembershipRepository;
import com.example.is_curse_work.repository.ZoneRepository;
import com.example.is_curse_work.security.CustomUserDetails;
import com.example.is_curse_work.service.FridgeService;
import com.example.is_curse_work.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductWebController {

    private final ProductService productService;
    private final CategoryRepository categories;
    private final ZoneRepository zones;
    private final FridgeMembershipRepository memberships;
    private final FridgeService fridges;

    public ProductWebController(ProductService productService,
                                CategoryRepository categories,
                                ZoneRepository zones,
                                FridgeMembershipRepository memberships,
                                FridgeService fridges) {
        this.productService = productService;
        this.categories = categories;
        this.zones = zones;
        this.memberships = memberships;
        this.fridges = fridges;
    }

    @GetMapping("/my")
    public String my(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        model.addAttribute("products", productService.getMyProducts(me.getUserId()));
        model.addAttribute("defaultFridgeId", resolveDefaultFridgeId(me.getUserId()));
        return "products/my";
    }

    @GetMapping("/add")
    public String addForm(@AuthenticationPrincipal CustomUserDetails me,
                          @RequestParam(name = "fridgeId", required = false) Long fridgeId,
                          Model model) {
        Long resolvedFridgeId = fridgeId != null ? fridgeId : resolveDefaultFridgeId(me.getUserId());
        model.addAttribute("form", new AddProductForm());
        model.addAttribute("categories", categories.findAll());
        model.addAttribute("fridges", fridges.listFridges());
        if (resolvedFridgeId != null) {
            model.addAttribute("zones", zones.findByFridge_FridgeIdOrderBySortOrderAsc(resolvedFridgeId));
        } else {
            model.addAttribute("error", "Сначала присоединитесь к холодильнику.");
        }
        model.addAttribute("fridgeId", resolvedFridgeId);
        return "products/add";
    }

    @PostMapping("/add")
    public String add(@AuthenticationPrincipal CustomUserDetails me,
                      @Valid @ModelAttribute("form") AddProductForm form,
                      BindingResult br,
                      @RequestParam(name = "fridgeId", required = false) Long fridgeId,
                      Model model) {
        if (br.hasErrors()) {
            Long resolvedFridgeId = fridgeId != null ? fridgeId : resolveDefaultFridgeId(me.getUserId());
            model.addAttribute("categories", categories.findAll());
            model.addAttribute("fridges", fridges.listFridges());
            if (resolvedFridgeId != null) {
                model.addAttribute("zones", zones.findByFridge_FridgeIdOrderBySortOrderAsc(resolvedFridgeId));
            }
            model.addAttribute("fridgeId", resolvedFridgeId);
            return "products/add";
        }
        if (form.getZoneId() != null) {
            var zone = zones.findById(form.getZoneId()).orElse(null);
            if (zone == null || !memberships.existsByFridgeIdAndUserIdAndLeftAtIsNull(zone.getFridge().getFridgeId(), me.getUserId())) {
                model.addAttribute("error", "Вы не состоите в холодильнике выбранной зоны.");
                model.addAttribute("categories", categories.findAll());
                model.addAttribute("fridges", fridges.listFridges());
                if (fridgeId != null) {
                    model.addAttribute("zones", zones.findByFridge_FridgeIdOrderBySortOrderAsc(fridgeId));
                }
                model.addAttribute("fridgeId", fridgeId);
                return "products/add";
            }
        }
        try {
            productService.addProduct(me.getUserId(), form);
            return "redirect:/products/my";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", categories.findAll());
            model.addAttribute("fridges", fridges.listFridges());
            if (fridgeId != null) model.addAttribute("zones", zones.findByFridge_FridgeIdOrderBySortOrderAsc(fridgeId));
            model.addAttribute("fridgeId", fridgeId);
            return "products/add";
        }
    }

    @GetMapping("/{id}")
    public String detail(@AuthenticationPrincipal CustomUserDetails me, @PathVariable("id") Long id, Model model) {
        model.addAttribute("p", productService.getProductDetail(me.getUserId(), id));
        model.addAttribute("moveForm", new MoveProductForm());
        model.addAttribute("extendForm", new ExtendProductForm());
        model.addAttribute("statusForm", new SetStatusForm());
        return "products/detail";
    }

    @PostMapping("/{id}/move")
    public String move(@AuthenticationPrincipal CustomUserDetails me, @PathVariable("id") Long id,
                       @Valid @ModelAttribute("moveForm") MoveProductForm form, BindingResult br) {
        if (br.hasErrors()) return "redirect:/products/" + id;
        productService.move(me.getUserId(), id, form);
        return "redirect:/products/" + id;
    }

    @PostMapping("/{id}/extend")
    public String extend(@AuthenticationPrincipal CustomUserDetails me, @PathVariable("id") Long id,
                         @Valid @ModelAttribute("extendForm") ExtendProductForm form, BindingResult br) {
        if (br.hasErrors()) return "redirect:/products/" + id;
        productService.extend(me.getUserId(), id, form);
        return "redirect:/products/" + id;
    }

    @PostMapping("/{id}/status")
    public String status(@AuthenticationPrincipal CustomUserDetails me, @PathVariable("id") Long id,
                         @Valid @ModelAttribute("statusForm") SetStatusForm form, BindingResult br) {
        if (br.hasErrors()) return "redirect:/products/" + id;
        productService.setStatus(me.getUserId(), id, form);
        return "redirect:/products/" + id;
    }

    private Long resolveDefaultFridgeId(Long userId) {
        return memberships.findByUserIdAndLeftAtIsNull(userId).stream()
                .findFirst()
                .map(m -> m.getFridgeId())
                .orElse(null);
    }
}
