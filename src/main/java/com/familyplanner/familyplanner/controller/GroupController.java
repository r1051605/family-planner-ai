package com.familyplanner.familyplanner.controller;

import com.familyplanner.familyplanner.model.Group;
import com.familyplanner.familyplanner.model.GroupMember;
import com.familyplanner.familyplanner.model.User;
import com.familyplanner.familyplanner.service.GroupService;
import com.familyplanner.familyplanner.service.ThemeService;
import com.familyplanner.familyplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;
    private final UserService userService;
    private final ThemeService themeService;

    @Autowired
    public GroupController(GroupService groupService, UserService userService, ThemeService themeService) {
        this.groupService = groupService;
        this.userService = userService;
        this.themeService = themeService;
    }

    @GetMapping
    public String viewGroups(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        model.addAttribute("user", currentUser);

        // Get all groups for the current family
        List<Group> groups = groupService.getGroupsByFamily(currentUser.getFamily());
        model.addAttribute("groups", groups);

        // Add a new group model for the form
        model.addAttribute("newGroup", new Group());

        // Get upcoming visits (group members with visit dates in the future)
        List<GroupMember> upcomingVisits = groupService.getUpcomingVisits(currentUser.getFamily());
        model.addAttribute("upcomingVisits", upcomingVisits);

        model.addAttribute("theme", themeService.getThemeForUser(currentUser));
        model.addAttribute("themeClass", themeService.getThemeClassForUser(currentUser));

        return "groups/groups";
    }

    @PostMapping("/create")
    public String createGroup(@ModelAttribute("newGroup") Group group,
                              BindingResult result,
                              Authentication authentication) {

        if (result.hasErrors()) {
            return "groups/groups";
        }

        User currentUser = userService.findByUsername(authentication.getName());

        group.setCreatedBy(currentUser);
        group.setFamily(currentUser.getFamily());

        groupService.saveGroup(group);

        return "redirect:/groups";
    }

    @GetMapping("/{id}")
    public String viewGroup(@PathVariable Long id, Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        Group group = groupService.getGroupById(id);

        // Check if group belongs to user's family
        if (!group.getFamily().equals(currentUser.getFamily())) {
            return "redirect:/groups?error=nopermission";
        }

        model.addAttribute("user", currentUser);
        model.addAttribute("group", group);

        // Add a new group member model for the form
        model.addAttribute("newMember", new GroupMember());

        model.addAttribute("theme", themeService.getThemeForUser(currentUser));
        model.addAttribute("themeClass", themeService.getThemeClassForUser(currentUser));

        return "groups/group-detail";
    }

    @PostMapping("/{groupId}/members/add")
    public String addGroupMember(@PathVariable Long groupId,
                                 @ModelAttribute("newMember") GroupMember member,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime visitDate,
                                 BindingResult result,
                                 Authentication authentication) {

        if (result.hasErrors()) {
            return "redirect:/groups/" + groupId + "?error=invalid";
        }

        User currentUser = userService.findByUsername(authentication.getName());
        Group group = groupService.getGroupById(groupId);

        // Check if group belongs to user's family
        if (!group.getFamily().equals(currentUser.getFamily())) {
            return "redirect:/groups?error=nopermission";
        }

        member.setGroup(group);
        member.setAddedBy(currentUser);
        member.setVisitDate(visitDate);

        groupService.saveGroupMember(member);

        return "redirect:/groups/" + groupId;
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<Long> deleteGroupMember(@PathVariable Long id, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        GroupMember member = groupService.getGroupMemberById(id);

        // Check if group belongs to user's family
        if (!member.getGroup().getFamily().equals(currentUser.getFamily())) {
            return ResponseEntity.badRequest().build();
        }

        Long groupId = member.getGroup().getId();
        groupService.deleteGroupMember(id);

        return ResponseEntity.ok(groupId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        Group group = groupService.getGroupById(id);

        // Check if group belongs to user's family and user is creator or parent
        if (group.getFamily().equals(currentUser.getFamily()) &&
                (group.getCreatedBy().equals(currentUser) || currentUser.getRole() == User.Role.PARENT)) {

            groupService.deleteGroup(id);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }
}