package com.familyplanner.familyplanner.controller;

import com.familyplanner.familyplanner.model.ShoppingItem;
import com.familyplanner.familyplanner.model.User;
import com.familyplanner.familyplanner.service.ShoppingService;
import com.familyplanner.familyplanner.service.ThemeService;
import com.familyplanner.familyplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/shopping")
public class ShoppingController {

    private final ShoppingService shoppingService;
    private final UserService userService;
    private final ThemeService themeService;

    @Autowired
    public ShoppingController(ShoppingService shoppingService, UserService userService, ThemeService themeService) {
        this.shoppingService = shoppingService;
        this.userService = userService;
        this.themeService = themeService;
    }

    @GetMapping
    public String viewShoppingList(Model model, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        model.addAttribute("user", currentUser);

        // Get shopping items for the current family
        List<ShoppingItem> shoppingItems = shoppingService.getItemsByFamily(currentUser.getFamily());
        model.addAttribute("shoppingItems", shoppingItems);

        // Group items by category
        Map<String, List<ShoppingItem>> itemsByCategory = shoppingService.groupItemsByCategory(shoppingItems);
        model.addAttribute("itemsByCategory", itemsByCategory);

        // Add a new shopping item model for the form
        model.addAttribute("newItem", new ShoppingItem());

        // Get available categories
        model.addAttribute("categories", shoppingService.getAvailableCategories());

        model.addAttribute("theme", themeService.getThemeForUser(currentUser));
        model.addAttribute("themeClass", themeService.getThemeClassForUser(currentUser));

        return "shopping/shopping";
    }

    @PostMapping("/add")
    public String addItem(@ModelAttribute("newItem") ShoppingItem item,
                          BindingResult result,
                          Authentication authentication) {

        if (result.hasErrors()) {
            return "shopping/shopping";
        }

        User currentUser = userService.findByUsername(authentication.getName());

        // Set default values
        item.setAddedBy(currentUser);
        item.setFamily(currentUser.getFamily());
        item.setBought(false);

        // If no category is selected, use "Other"
        if (item.getCategory() == null || item.getCategory().isEmpty()) {
            item.setCategory("Other");
        }

        shoppingService.saveItem(item);

        return "redirect:/shopping";
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<Void> toggleItemBought(@PathVariable Long id, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        ShoppingItem item = shoppingService.getItemById(id);

        // Check if item belongs to user's family
        if (item.getFamily().equals(currentUser.getFamily())) {
            item.setBought(!item.isBought());
            shoppingService.saveItem(item);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id, Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        ShoppingItem item = shoppingService.getItemById(id);

        // Check if item belongs to user's family
        if (item.getFamily().equals(currentUser.getFamily())) {
            shoppingService.deleteItem(id);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/clear-bought")
    public String clearBoughtItems(Authentication authentication) {
        User currentUser = userService.findByUsername(authentication.getName());
        shoppingService.deleteBoughtItems(currentUser.getFamily());

        return "redirect:/shopping";
    }
}