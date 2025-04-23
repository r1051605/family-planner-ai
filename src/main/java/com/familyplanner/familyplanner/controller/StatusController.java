package com.familyplanner.familyplanner.controller;
import com.familyplanner.familyplanner.model.User;
import com.familyplanner.familyplanner.model.UserStatus;
import com.familyplanner.familyplanner.service.StatusService;
import com.familyplanner.familyplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/status")
public class StatusController {

    private final StatusService statusService;
    private final UserService userService;

    @Autowired
    public StatusController(StatusService statusService, UserService userService) {
        this.statusService = statusService;
        this.userService = userService;
    }

    @PostMapping("/update")
    public String updateStatus(@RequestParam("status") UserStatus.Status status,
                               Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        statusService.updateUserStatus(currentUser, status);

        return "redirect:/home";
    }
}