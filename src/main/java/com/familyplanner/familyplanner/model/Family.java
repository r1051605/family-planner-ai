package com.familyplanner.familyplanner.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "families")
public class Family {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(nullable = false)
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<User> members = new HashSet<>();

    // Constructors
    public Family() {
    }

    public Family(Long id, String name, User owner, LocalDateTime creationDate) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.creationDate = creationDate;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    // Helper methods
    public void addMember(User user) {
        members.add(user);
        user.setFamily(this);
    }

    public void removeMember(User user) {
        members.remove(user);
        user.setFamily(null);
    }
}