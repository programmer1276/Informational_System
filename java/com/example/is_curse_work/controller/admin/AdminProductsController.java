package com.example.is_curse_work.controller.admin;

import com.example.is_curse_work.dto.AdminProductFilter;
import com.example.is_curse_work.dto.SetStatusForm;
import com.example.is_curse_work.repository.FridgeRepository;
import com.example.is_curse_work.repository.ProductRepository;
import com.example.is_curse_work.security.CustomUserDetails;
import com.example.is_curse_work.service.AuditService;
import com.example.is_curse_work.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/products")
public class AdminProductsController {

    private final ProductRepository products;
    private final FridgeRepository fridges;
    private final ProductService productService;
    private final AuditService audit;

    public AdminProductsController(ProductRepository products,
                                   FridgeRepository fridges,
                                   ProductService productService,
                                   AuditService audit) {
        this.products = products;
        this.fridges = fridges;
        this.productService = productService;
        this.audit = audit;
    }

    @PostMapping("/bulk-delete")
    public String bulkDelete(@AuthenticationPrincipal CustomUserDetails actor,
                             @RequestParam(name = "ids", required = false) java.util.List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return "redirect:/admin/products";
        }
        try {
            // delete in batch for performance
            products.deleteAllByIdInBatch(ids);
            for (Long id : ids) {
                audit.log(actor.getUsername(), "DELETE_PRODUCT", "Product", id.toString(), "Deleted product (bulk)");
            }
        } catch (Exception e) {
            // if batch delete fails for some reason, fallback to individual deletes and audit
            for (Long id : ids) {
                try {
                    products.deleteById(id);
                    audit.log(actor.getUsername(), "DELETE_PRODUCT", "Product", id.toString(), "Deleted product (bulk-fallback)");
                } catch (Exception ignored) {}
            }
        }

        return "redirect:/admin/products";
    }

    @GetMapping
    public String list(@ModelAttribute("filter") AdminProductFilter filter, Model model) {
        model.addAttribute("fridges", fridges.findAll());
        var items = products.searchAdmin(normalize(filter.getOwnerEmail()),
                normalize(filter.getStatus()),
                filter.getFridgeId());
        model.addAttribute("products", items == null ? java.util.List.of() : items);
        model.addAttribute("statusForm", new SetStatusForm());
        return "admin/products";
    }

    @PostMapping("/{id}/status")
    public String setStatus(@AuthenticationPrincipal CustomUserDetails actor,
                            @PathVariable("id") Long id,
                            @Valid @ModelAttribute("statusForm") SetStatusForm form,
                            BindingResult br,
                            @ModelAttribute("filter") AdminProductFilter filter,
                            Model model) {
        if (br.hasErrors()) {
            return list(filter, model);
        }
        productService.setStatus(actor.getUserId(), id, form);
        audit.log(actor.getUsername(), "ADMIN_PRODUCT_STATUS", "Product", id.toString(), form.getStatus());
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal CustomUserDetails actor, @PathVariable("id") Long id) {
        products.deleteById(id);
        audit.log(actor.getUsername(), "DELETE_PRODUCT", "Product", id.toString(), "Deleted product");
        return "redirect:/admin/products";
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
