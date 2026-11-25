package com.bidzone.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Abstract base class representing a generic user in the BidZone system.
 * Demonstrates Abstraction, Encapsulation, and Single Inheritance.
 */
public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role;
    private LocalDateTime createdAt;
    private boolean active;

    /**
     * Overloaded constructor 1: Basic user creation without phone.
     * Demonstrates Constructor Chaining using `this(...)`.
     */
    public User(String id, String username, String password, String fullName, String email, UserRole role) {
        this(id, username, password, fullName, email, "N/A", role);
    }

    /**
     * Overloaded constructor 2: Full user creation.
     * Resolves variable shadowing using the `this` keyword.
     */
    public User(String id, String username, String password, String fullName, String email, String phone, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = (phone == null || phone.trim().isEmpty()) ? "N/A" : phone;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    // Abstract methods demonstrating Abstraction and Polymorphism
    public abstract void displayProfile();
    public abstract String getRoleSpecificDetails();

    // Getters and Setters with Encapsulation
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return String.format("[%s] ID: %s | Username: %s | Name: %s | Email: %s | Status: %s",
                role.name(), id, username, fullName, email, active ? "Active" : "Suspended");
    }
}