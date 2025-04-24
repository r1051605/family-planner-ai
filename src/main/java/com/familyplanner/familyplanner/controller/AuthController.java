package com.familyplanner.familyplanner.controller;

import com.familyplanner.familyplanner.model.Family;
import com.familyplanner.familyplanner.model.User;
import com.familyplanner.familyplanner.service.FamilyService;
import com.familyplanner.familyplanner.service.StatusService;
import com.familyplanner.familyplanner.service.ThemeService;
import com.familyplanner.familyplanner.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class AuthController {

    private final UserService userService;
    private final FamilyService familyService;
    private final StatusService statusService;
    private final ThemeService themeService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(UserService userService,
                          FamilyService familyService,
                          StatusService statusService,
                          ThemeService themeService,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.familyService = familyService;
        this.statusService = statusService;
        this.themeService = themeService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("theme", themeService.getThemeForUser(null));
        model.addAttribute("themeClass", themeService.getThemeClassForUser(null));
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("theme", themeService.getThemeForUser(null));
        model.addAttribute("themeClass", themeService.getThemeClassForUser(null));
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user,
                               BindingResult result,
                               @RequestParam(required = false) String familyCode,
                               HttpServletRequest request,
                               RedirectAttributes redirectAttributes) {

        String rawPassword = user.getPassword();

        if (result.hasErrors()) {
            return "auth/register";
        }

        if (userService.existsByUsername(user.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Username already exists");
            return "redirect:/register";
        }

        if (familyCode != null && !familyCode.isEmpty()) {
            Family family = familyService.getFamilyByCode(familyCode);

            if (family == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid family code");
                return "redirect:/register";
            }

            user.setFamily(family);
            if (user.getRole() == null) {
                user.setRole(User.Role.KID);
            }

            user = userService.saveUser(user);
        } else {
            user.setRole(User.Role.PARENT);
            user = userService.saveUser(user);  // ✅ Save user first

            Family family = new Family();
            family.setName(user.getUsername() + "'s Family");
            family.setCreationDate(LocalDateTime.now());
            family.setOwner(user);

            family = familyService.saveFamily(family); // ✅ Save family after assigning the saved user
            user.setFamily(family);
            user = userService.saveUser(user);  // ✅ Update user with family
        }

        statusService.initializeUserStatus(user);
        autoLogin(user.getUsername(), rawPassword, request);

        if (familyCode == null || familyCode.isEmpty()) {
            return "redirect:/family/setup";
        }

        return "redirect:/home";
    }

    private void autoLogin(String username, String password, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        token.setDetails(new WebAuthenticationDetails(request));

        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
