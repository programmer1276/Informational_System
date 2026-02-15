package com.example.is_curse_work.controller;

import com.example.is_curse_work.repository.FridgeMembershipRepository;
import com.example.is_curse_work.repository.FridgeRepository;
import com.example.is_curse_work.security.CustomUserDetails;
import com.example.is_curse_work.service.FridgeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/fridges")
public class FridgeWebController {

    private final FridgeService fridgeService;
    private final FridgeRepository fridges;
    private final FridgeMembershipRepository memberships;

    public FridgeWebController(FridgeService fridgeService,
                               FridgeRepository fridges,
                               FridgeMembershipRepository memberships) {
        this.fridgeService = fridgeService;
        this.fridges = fridges;
        this.memberships = memberships;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("fridges", fridgeService.listFridges());
        return "fridges/list";
    }

    @PostMapping("/{id}/join")
    public String join(@AuthenticationPrincipal CustomUserDetails me, @PathVariable("id") Long id) {
        fridgeService.joinFridge(id, me.getUserId());
        return "redirect:/fridges/" + id + "/map";
    }

    @GetMapping("/{id}/join")
    public String joinGet(@AuthenticationPrincipal CustomUserDetails me, @PathVariable("id") Long id) {
        fridgeService.joinFridge(id, me.getUserId());
        return "redirect:/fridges/" + id + "/map";
    }

    @GetMapping("/{id}/map")
    public String map(@PathVariable("id") Long id, Model model) {
        model.addAttribute("map", fridgeService.getMap(id));
        model.addAttribute("fridgeId", id);
        return "fridges/map";
    }

    @GetMapping("/{id}/members")
    public String members(@AuthenticationPrincipal CustomUserDetails me,
                          @PathVariable("id") Long id,
                          Model model) {
        if (!canViewMembers(id, me)) {
            return "redirect:/fridges";
        }
        model.addAttribute("canManageMembers", canManageMembers(id, me));
        model.addAttribute("members", fridgeService.listMembers(id));
        model.addAttribute("fridgeId", id);
        return "fridges/members";
    }

    @PostMapping("/{id}/members/{userId}/remove")
    public String removeMember(@AuthenticationPrincipal CustomUserDetails me,
                               @PathVariable("id") Long id,
                               @PathVariable("userId") Long userId) {
        if (!canManageMembers(id, me)) {
            return "redirect:/fridges/" + id + "/members";
        }
        fridgeService.removeMember(id, userId);
        return "redirect:/fridges/" + id + "/members";
    }

    private boolean canViewMembers(Long fridgeId, CustomUserDetails me) {
        if (me == null) return false;
        if (hasRole(me, "ADMIN") || hasRole(me, "MODERATOR")) return true;
        if (isOwner(fridgeId, me)) return true;
        return memberships.existsByFridgeIdAndUserIdAndLeftAtIsNull(fridgeId, me.getUserId());
    }

    private boolean canManageMembers(Long fridgeId, CustomUserDetails me) {
        if (me == null) return false;
        if (hasRole(me, "ADMIN") || hasRole(me, "MODERATOR")) return true;
        return isOwner(fridgeId, me);
    }

    private boolean isOwner(Long fridgeId, CustomUserDetails me) {
        return fridges.findById(fridgeId)
                .map(fridge -> fridge.getOwnerId() != null && fridge.getOwnerId().equals(me.getUserId()))
                .orElse(false);
    }

    private boolean hasRole(CustomUserDetails me, String role) {
        return me.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}
