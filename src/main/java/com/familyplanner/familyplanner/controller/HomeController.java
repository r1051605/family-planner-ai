package com.familyplanner.familyplanner.controller;

import com.familyplanner.familyplanner.model.User;
import com.familyplanner.familyplanner.service.ThemeService;
import com.familyplanner.familyplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserService userService;
    private final ThemeService themeService;

    @Autowired
    public HomeController(UserService userService, ThemeService themeService) {
        this.userService = userService;
        this.themeService = themeService;
    }

    @GetMapping("/")
    public String index(Model model, Authentication authentication) {
        User currentUser = null;

        if (authentication != null && authentication.isAuthenticated()) {
            currentUser = userService.findByUsername(authentication.getName());
            model.addAttribute("user", currentUser);
        }

        model.addAttribute("theme", themeService.getThemeForUser(currentUser));
        model.addAttribute("themeClass", themeService.getThemeClassForUser(currentUser));

        return "index";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        model.addAttribute("user", currentUser);
        model.addAttribute("theme", themeService.getThemeForUser(currentUser));
        model.addAttribute("themeClass", themeService.getThemeClassForUser(currentUser));

        return "home";
    }
}