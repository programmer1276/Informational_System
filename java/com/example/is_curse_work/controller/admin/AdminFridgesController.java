package com.example.is_curse_work.controller.admin;

import com.example.is_curse_work.dto.AdminFridgeForm;
import com.example.is_curse_work.model.Fridge;
import com.example.is_curse_work.repository.FridgeRepository;
import com.example.is_curse_work.security.CustomUserDetails;
import com.example.is_curse_work.service.AuditService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/fridges")
public class AdminFridgesController {

    private final FridgeRepository fridges;
    private final AuditService audit;

    public AdminFridgesController(FridgeRepository fridges, AuditService audit) {
        this.fridges = fridges;
        this.audit = audit;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("fridges", fridges.findAll());
        model.addAttribute("form", new AdminFridgeForm());
        return "admin/fridges";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal CustomUserDetails actor,
                         @Valid @ModelAttribute("form") AdminFridgeForm form,
                         BindingResult br,
                         Model model) {
        if (br.hasErrors()) {
            model.addAttribute("fridges", fridges.findAll());
            return "admin/fridges";
        }
        Fridge fridge = new Fridge();
        fridge.setName(form.getName());
        fridge.setLocation(form.getLocation());
        fridge.setInviteRequired(form.isInviteRequired());
        fridge.setOwnerId(actor.getUserId());
        Fridge saved = fridges.save(fridge);
        audit.log(actor.getUsername(), "CREATE_FRIDGE", "Fridge", saved.getFridgeId().toString(), saved.getName());
        return "redirect:/admin/fridges";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Fridge fridge = fridges.findById(id).orElseThrow();
        AdminFridgeForm form = new AdminFridgeForm();
        form.setName(fridge.getName());
        form.setLocation(fridge.getLocation());
        form.setInviteRequired(fridge.isInviteRequired());
        model.addAttribute("fridgeId", id);
        model.addAttribute("form", form);
        return "admin/fridge-edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@AuthenticationPrincipal CustomUserDetails actor,
                         @PathVariable("id") Long id,
                         @Valid @ModelAttribute("form") AdminFridgeForm form,
                         BindingResult br,
                         Model model) {
        if (br.hasErrors()) {
            model.addAttribute("fridgeId", id);
            return "admin/fridge-edit";
        }
        Fridge fridge = fridges.findById(id).orElseThrow();
        fridge.setName(form.getName());
        fridge.setLocation(form.getLocation());
        fridge.setInviteRequired(form.isInviteRequired());
        fridges.save(fridge);
        audit.log(actor.getUsername(), "UPDATE_FRIDGE", "Fridge", id.toString(), fridge.getName());
        return "redirect:/admin/fridges";
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal CustomUserDetails actor, @PathVariable("id") Long id) {
        Fridge fridge = fridges.findById(id).orElseThrow();
        fridges.delete(fridge);
        audit.log(actor.getUsername(), "DELETE_FRIDGE", "Fridge", id.toString(), fridge.getName());
        return "redirect:/admin/fridges";
    }
}
