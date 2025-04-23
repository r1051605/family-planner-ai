package com.familyplanner.familyplanner.service;

import com.familyplanner.familyplanner.model.Family;
import com.familyplanner.familyplanner.model.ShoppingItem;
import com.familyplanner.familyplanner.model.User;
import com.familyplanner.familyplanner.repository.ShoppingItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShoppingService {

    private final ShoppingItemRepository shoppingItemRepository;

    @Autowired
    public ShoppingService(ShoppingItemRepository shoppingItemRepository) {
        this.shoppingItemRepository = shoppingItemRepository;
    }

    public List<ShoppingItem> getItemsByFamily(Family family) {
        return shoppingItemRepository.findByFamilyOrderByBoughtAscCategoryAsc(family);
    }

    public ShoppingItem getItemById(Long id) {
        return shoppingItemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Shopping item not found"));
    }

    public ShoppingItem saveItem(ShoppingItem item) {
        return shoppingItemRepository.save(item);
    }

    public void deleteItem(Long id) {
        shoppingItemRepository.deleteById(id);
    }

    public void deleteBoughtItems(Family family) {
        List<ShoppingItem> boughtItems = shoppingItemRepository.findByFamilyAndBoughtTrue(family);
        shoppingItemRepository.deleteAll(boughtItems);
    }

    public Map<String, List<ShoppingItem>> groupItemsByCategory(List<ShoppingItem> items) {
        // Group items by category
        Map<String, List<ShoppingItem>> groupedItems = items.stream()
                .collect(Collectors.groupingBy(ShoppingItem::getCategory));

        // Sort categories alphabetically but with "Other" at the end
        Map<String, List<ShoppingItem>> sortedGroups = new LinkedHashMap<>();

        groupedItems.entrySet().stream()
                .sorted((e1, e2) -> {
                    if (e1.getKey().equals("Other")) return 1;
                    if (e2.getKey().equals("Other")) return -1;
                    return e1.getKey().compareTo(e2.getKey());
                })
                .forEach(entry -> sortedGroups.put(entry.getKey(), entry.getValue()));

        return sortedGroups;
    }

    public List<String> getAvailableCategories() {
        // Default shopping categories
        List<String> categories = new ArrayList<>(Arrays.asList(
                "Fruits & Vegetables",
                "Dairy & Eggs",
                "Meat & Fish",
                "Bakery",
                "Pantry",
                "Frozen Foods",
                "Beverages",
                "Snacks",
                "Household",
                "Personal Care",
                "Baby Items",
                "Pet Supplies",
                "Other"
        ));

        return categories;
    }

    // Method to initialize default shopping items for a new family
    public void initializeDefaultItems(Family family, User creator) {
        createDefaultItem("Milk", 1, "Dairy & Eggs", family, creator);
        createDefaultItem("Bread", 1, "Bakery", family, creator);
        createDefaultItem("Eggs", 12, "Dairy & Eggs", family, creator);
        createDefaultItem("Bananas", 5, "Fruits & Vegetables", family, creator);
        createDefaultItem("Pasta", 1, "Pantry", family, creator);
    }

    private void createDefaultItem(String name, Integer quantity, String category, Family family, User creator) {
        ShoppingItem item = new ShoppingItem();
        item.setName(name);
        item.setQuantity(quantity);
        item.setCategory(category);
        item.setFamily(family);
        item.setAddedBy(creator);
        item.setBought(false);

        shoppingItemRepository.save(item);
    }
}