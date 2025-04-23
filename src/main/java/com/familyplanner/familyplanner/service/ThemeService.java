package com.familyplanner.familyplanner.service;

import com.familyplanner.familyplanner.model.User;
import org.springframework.stereotype.Service;

@Service
public class ThemeService {

    /**
     * Get theme CSS file based on user role
     */
    public String getThemeForUser(User user) {
        if (user == null) {
            return "css/default-theme.css"; // Default theme for guests
        }

        return switch (user.getRole()) {
            case PARENT -> "css/parent-theme.css"; // Dark, clean, minimalistic
            case TEEN -> "css/teen-theme.css"; // More playful
            case KID -> "css/kid-theme.css"; // Colorful and fun
        };
    }

    /**
     * Get theme CSS class name based on user role
     */
    public String getThemeClassForUser(User user) {
        if (user == null) {
            return "default-theme"; // Default theme class for guests
        }

        return switch (user.getRole()) {
            case PARENT -> "parent-theme";
            case TEEN -> "teen-theme";
            case KID -> "kid-theme";
        };
    }
}