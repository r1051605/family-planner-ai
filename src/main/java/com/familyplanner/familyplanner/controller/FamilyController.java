package com.familyplanner.familyplanner.controller;
import com.familyplanner.familyplanner.model.Family;
import com.familyplanner.familyplanner.model.User;
import com.familyplanner.familyplanner.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/family")
public class FamilyController {

    private final FamilyService familyService;
    private final UserService userService;
    private final ThemeService themeService;
    private final TaskService taskService;
    private final ShoppingService shoppingService;
    private final GroupService groupService;

    @Autowired
    public FamilyController(FamilyService familyService, UserService userService,
                            ThemeService themeService, TaskService taskService,
                            ShoppingService shoppingService, GroupService groupService) {
        this.familyService = familyService;
        this.userService = userService;
        this.themeService = themeService;
        this.taskService = taskService;
        this.shoppingService = shoppingService;
        this.groupService = groupService;
    }

    @GetMapping("/setup")
    public String setupFamily(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        model.addAttribute("user", currentUser);
        model.addAttribute("family", currentUser.getFamily());

        model.addAttribute("theme", themeService.getThemeForUser(currentUser));
        model.addAttribute("themeClass", themeService.getThemeClassForUser(currentUser));

        return "family/setup";
    }

    @PostMapping("/setup/complete")
    public String completeSetup(@RequestParam String familyName, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        Family family = currentUser.getFamily();

        // Update family name
        family.setName(familyName);
        familyService.saveFamily(family);

        // Initialize default data for the family
        taskService.initializeDefaultTasks(family, currentUser);
        shoppingService.initializeDefaultItems(family, currentUser);
        groupService.initializeDefaultGroups(family, currentUser);

        return "redirect:/home";
    }

    @GetMapping("/invite")
    public String showInviteForm(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());

        // Only family owners or parents can invite
        if (currentUser.getRole() != User.Role.PARENT) {
            return "redirect:/home?error=nopermission";
        }

        Family family = currentUser.getFamily();
        model.addAttribute("user", currentUser);
        model.addAttribute("family", family);
        model.addAttribute("inviteCode", family.getId().toString());

        model.addAttribute("theme", themeService.getThemeForUser(currentUser));
        model.addAttribute("themeClass", themeService.getThemeClassForUser(currentUser));

        return "family/invite";
    }

    @GetMapping("/members")
    public String manageMembers(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());

        // Only family owners or parents can manage members
        if (currentUser.getRole() != User.Role.PARENT) {
            return "redirect:/home?error=nopermission";
        }

        Family family = currentUser.getFamily();
        model.addAttribute("user", currentUser);
        model.addAttribute("family", family);
        model.addAttribute("members", userService.getFamilyMembers(family));

        model.addAttribute("theme", themeService.getThemeForUser(currentUser));
        model.addAttribute("themeClass", themeService.getThemeClassForUser(currentUser));

        return "family/members";
    }

    @PostMapping("/members/{memberId}/role")
    public String updateMemberRole(@PathVariable Long memberId,
                                   @RequestParam User.Role role,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {

        User currentUser = userService.findByUsername(authentication.getName());

        // Only family owners or parents can update roles
        if (currentUser.getRole() != User.Role.PARENT) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to update roles");
            return "redirect:/family/members";
        }

        User member = userService.findById(memberId);

        // Check if member is in the same family
        if (!member.getFamily().equals(currentUser.getFamily())) {
            redirectAttributes.addFlashAttribute("error", "Member not found in your family");
            return "redirect:/family/members";
        }

        // Cannot change role of family owner
        if (member.equals(member.getFamily().getOwner())) {
            redirectAttributes.addFlashAttribute("error", "Cannot change the role of the family owner");
            return "redirect:/family/members";
        }

        // Update role
        member.setRole(role);
        userService.saveUser(member);

        redirectAttributes.addFlashAttribute("success", "Member role updated successfully");
        return "redirect:/family/members";
    }

    @PostMapping("/members/{memberId}/remove")
    public String removeMember(@PathVariable Long memberId,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {

        User currentUser = userService.findByUsername(authentication.getName());

        // Only family owners or parents can remove members
        if (currentUser.getRole() != User.Role.PARENT) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to remove members");
            return "redirect:/family/members";
        }

        User member = userService.findById(memberId);

        // Check if member is in the same family
        if (!member.getFamily().equals(currentUser.getFamily())) {
            redirectAttributes.addFlashAttribute("error", "Member not found in your family");
            return "redirect:/family/members";
        }

        // Cannot remove family owner
        if (member.equals(member.getFamily().getOwner())) {
            redirectAttributes.addFlashAttribute("error", "Cannot remove the family owner");
            return "redirect:/family/members";
        }

        // Remove from family
        member.setFamily(null);
        userService.saveUser(member);

        redirectAttributes.addFlashAttribute("success", "Member removed from family successfully");
        return "redirect:/family/members";
    }
}