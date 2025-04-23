package com.familyplanner.familyplanner.controller;
import com.familyplanner.familyplanner.model.Family;
import com.familyplanner.familyplanner.model.User;
import com.familyplanner.familyplanner.service.FamilyService;
import com.familyplanner.familyplanner.service.StatusService;
import com.familyplanner.familyplanner.service.ThemeService;
import com.familyplanner.familyplanner.service.UserService;
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

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Controller
public class AuthController {

    private final UserService userService;
    private final FamilyService familyService;
    private final StatusService statusService;
    private final ThemeService themeService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(UserService userService, FamilyService familyService,
                          StatusService statusService, ThemeService themeService,
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

        if (result.hasErrors()) {
            return "auth/register";
        }

        // Check if username already exists
        if (userService.existsByUsername(user.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "Username already exists");
            return "redirect:/register";
        }

        // Handle family assignment
        if (familyCode != null && !familyCode.isEmpty()) {
            // Join existing family
            Family family = familyService.getFamilyByCode(familyCode);

            if (family == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid family code");
                return "redirect:/register";
            }

            user.setFamily(family);

            // If no role specified, default to KID role for joining members
            if (user.getRole() == null) {
                user.setRole(User.Role.KID);
            }
        } else {
            // Create new family
            Family family = new Family();
            family.setName(user.getUsername() + "'s Family");
            family.setCreationDate(LocalDateTime.now());

            // Set user as PARENT (owner) for new families
            user.setRole(User.Role.PARENT);

            // Save family first to get ID
            family = familyService.saveFamily(family);

            // Set owner and family
            family.setOwner(user);
            user.setFamily(family);

            // Update family with owner
            family = familyService.saveFamily(family);
        }

        // Save the user
        user = userService.saveUser(user);

        // Initialize status
        statusService.initializeUserStatus(user);

        // Auto-login after registration
        autoLogin(user.getUsername(), user.getPassword(), request);

        // Redirect to family setup if created new family
        if (familyCode == null || familyCode.isEmpty()) {
            return "redirect:/family/setup";
        }

        return "redirect:/home";
    }

    // Method to auto-login after registration
    private void autoLogin(String username, String password, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        token.setDetails(new WebAuthenticationDetails(request));

        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}