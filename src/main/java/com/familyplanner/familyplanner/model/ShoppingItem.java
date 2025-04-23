package com.familyplanner.familyplanner.model;

import jakarta.persistence.*;

@Entity
@Table(name = "shopping_items")
public class ShoppingItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer quantity;

    private boolean bought;

    private String category;

    @ManyToOne
    @JoinColumn(name = "added_by", nullable = false)
    private User addedBy;

    @ManyToOne
    @JoinColumn(name = "family_id", nullable = false)
    private Family family;

    // Constructors
    public ShoppingItem() {
    }

    public ShoppingItem(Long id, String name, Integer quantity, boolean bought,
                        String category, User addedBy, Family family) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.bought = bought;
        this.category = category;
        this.addedBy = addedBy;
        this.family = family;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public User getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(User addedBy) {
        this.addedBy = addedBy;
    }

    public Family getFamily() {
        return family;
    }

    public void setFamily(Family family) {
        this.family = family;
    }
}