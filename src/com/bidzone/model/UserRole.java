package com.bidzone.model;

/**
 * Enumeration of system user roles for Role-Based Access Control (RBAC).
 */
public enum UserRole {
    ADMIN("Administrator"),
    SELLER("Seller"),
    BIDDER("Bidder");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
