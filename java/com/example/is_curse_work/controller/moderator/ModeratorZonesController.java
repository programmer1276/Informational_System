package com.example.is_curse_work.controller.moderator;

import com.example.is_curse_work.dto.ZoneForm;
import com.example.is_curse_work.model.Zone;
import com.example.is_curse_work.repository.FridgeRepository;
import com.example.is_curse_work.repository.ZoneRepository;
import com.example.is_curse_work.security.CustomUserDetails;
import com.example.is_curse_work.service.AuditService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/moderator/fridges/{fridgeId}/zones")
public class ModeratorZonesController {

    private final ZoneRepository zones;
    private final FridgeRepository fridges;
    private final AuditService audit;

    public ModeratorZonesController(ZoneRepository zones, FridgeRepository fridges, AuditService audit) {
        this.zones = zones;
        this.fridges = fridges;
        this.audit = audit;
    }

    @GetMapping
    public String list(@PathVariable("fridgeId") Long fridgeId, Model model) {
        var fridge = fridges.findById(fridgeId).orElseThrow();
        model.addAttribute("fridge", fridge);
        model.addAttribute("zones", zones.findByFridge_FridgeIdOrderBySortOrderAsc(fridgeId));
        model.addAttribute("form", new ZoneForm());
        return "moderator/zones";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal CustomUserDetails actor,
                         @PathVariable("fridgeId") Long fridgeId,
                         @Valid @ModelAttribute("form") ZoneForm form,
                         BindingResult br,
                         Model model) {
        if (br.hasErrors()) {
            var fridge = fridges.findById(fridgeId).orElseThrow();
            model.addAttribute("fridge", fridge);
            model.addAttribute("zones", zones.findByFridge_FridgeIdOrderBySortOrderAsc(fridgeId));
            return "moderator/zones";
        }
        var fridge = fridges.findById(fridgeId).orElseThrow();
        Zone zone = new Zone();
        zone.setFridge(fridge);
        zone.setName(form.getName());
        zone.setCapacityUnits(form.getCapacityUnits());
        zone.setCapacityVolumeL(form.getCapacityVolumeL());
        zone.setSortOrder(form.getSortOrder());
        zone.setActive(form.isActive());
        Zone saved = zones.save(zone);
        audit.log(actor.getUsername(), "CREATE_ZONE", "Zone", saved.getZoneId().toString(), saved.getName());
        return "redirect:/moderator/fridges/" + fridgeId + "/zones";
    }

    @GetMapping("/{zoneId}/edit")
    public String editForm(@PathVariable("fridgeId") Long fridgeId,
                           @PathVariable("zoneId") Long zoneId,
                           Model model) {
        var fridge = fridges.findById(fridgeId).orElseThrow();
        Zone zone = zones.findById(zoneId).orElseThrow();
        ZoneForm form = new ZoneForm();
        form.setName(zone.getName());
        form.setCapacityUnits(zone.getCapacityUnits());
        form.setCapacityVolumeL(zone.getCapacityVolumeL());
        form.setSortOrder(zone.getSortOrder());
        form.setActive(zone.isActive());
        model.addAttribute("fridge", fridge);
        model.addAttribute("zoneId", zoneId);
        model.addAttribute("form", form);
        return "moderator/zone-edit";
    }

    @PostMapping("/{zoneId}/edit")
    public String update(@AuthenticationPrincipal CustomUserDetails actor,
                         @PathVariable("fridgeId") Long fridgeId,
                         @PathVariable("zoneId") Long zoneId,
                         @Valid @ModelAttribute("form") ZoneForm form,
                         BindingResult br,
                         Model model) {
        if (br.hasErrors()) {
            var fridge = fridges.findById(fridgeId).orElseThrow();
            model.addAttribute("fridge", fridge);
            model.addAttribute("zoneId", zoneId);
            return "moderator/zone-edit";
        }
        Zone zone = zones.findById(zoneId).orElseThrow();
        zone.setName(form.getName());
        zone.setCapacityUnits(form.getCapacityUnits());
        zone.setCapacityVolumeL(form.getCapacityVolumeL());
        zone.setSortOrder(form.getSortOrder());
        zone.setActive(form.isActive());
        zones.save(zone);
        audit.log(actor.getUsername(), "UPDATE_ZONE", "Zone", zoneId.toString(), zone.getName());
        return "redirect:/moderator/fridges/" + fridgeId + "/zones";
    }

    @PostMapping("/{zoneId}/toggle")
    public String toggle(@AuthenticationPrincipal CustomUserDetails actor,
                         @PathVariable("fridgeId") Long fridgeId,
                         @PathVariable("zoneId") Long zoneId) {
        Zone zone = zones.findById(zoneId).orElseThrow();
        zone.setActive(!zone.isActive());
        zones.save(zone);
        audit.log(actor.getUsername(), "TOGGLE_ZONE", "Zone", zoneId.toString(), "Active=" + zone.isActive());
        return "redirect:/moderator/fridges/" + fridgeId + "/zones";
    }
}
