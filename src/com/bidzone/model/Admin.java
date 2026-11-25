package com.bidzone.model;

/**
 * Concrete class representing a System Administrator.
 * Inherits from User and provides administrative role details.
 */
public class Admin extends User {
    private static final long serialVersionUID = 1L;

    private String department;
    private String clearanceLevel;

    public Admin(String id, String username, String password, String fullName, String email, String department) {
        super(id, username, password, fullName, email, UserRole.ADMIN);
        this.department = department;
        this.clearanceLevel = "SUPER_ADMIN";
    }

    public Admin(String id, String username, String password, String fullName, String email, String phone, String department, String clearanceLevel) {
        super(id, username, password, fullName, email, phone, UserRole.ADMIN);
        this.department = department;
        this.clearanceLevel = clearanceLevel;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getClearanceLevel() {
        return clearanceLevel;
    }

    public void setClearanceLevel(String clearanceLevel) {
        this.clearanceLevel = clearanceLevel;
    }

    @Override
    public void displayProfile() {
        System.out.println("==================================================");
        System.out.println("               ADMINISTRATOR PROFILE              ");
        System.out.println("==================================================");
        System.out.printf("User ID         : %s%n", getId());
        System.out.printf("Username        : %s%n", getUsername());
        System.out.printf("Full Name       : %s%n", getFullName());
        System.out.printf("Email           : %s%n", getEmail());
        System.out.printf("Department      : %s%n", department);
        System.out.printf("Clearance Level : %s%n", clearanceLevel);
        System.out.printf("Account Created : %s%n", getCreatedAt());
        System.out.println("==================================================");
    }

    @Override
    public String getRoleSpecificDetails() {
        return String.format("Dept: %s | Clearance: %s", department, clearanceLevel);
    }
}
