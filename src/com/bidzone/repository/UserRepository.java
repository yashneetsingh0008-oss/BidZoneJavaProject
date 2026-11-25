package com.bidzone.repository;

import com.bidzone.model.Admin;
import com.bidzone.model.Bidder;
import com.bidzone.model.Seller;
import com.bidzone.model.User;
import com.bidzone.model.UserRole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Concrete repository managing User entities.
 * Combines in-memory HashMap caching with file serialization to "users.ser".
 * Demonstrates Collections Framework (HashMap, ArrayList) and File Persistence.
 */
public class UserRepository implements GenericRepository<User, String> {
    private static final String STORAGE_FILE = "users.ser";
    private final Map<String, User> userCache;

    public UserRepository() {
        this.userCache = new HashMap<>();
        loadData();
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        try {
            Object loaded = FileStorageManager.loadFromFile(STORAGE_FILE);
            if (loaded instanceof Map) {
                userCache.putAll((Map<String, User>) loaded);
            } else {
                seedInitialUsers();
            }
        } catch (Exception e) {
            System.err.println("[UserRepository] Note: Initializing default accounts (No prior user data found).");
            seedInitialUsers();
        }
    }

    private void seedInitialUsers() {
        // Default System Administrator
        Admin admin = new Admin("USR-1001", "admin", "admin123", "System Administrator", "admin@bidzone.com", "IT Operations");

        // Default Demo Seller
        Seller seller = new Seller("USR-1002", "john_seller", "seller123", "Johnathan Doe", "john@marketplace.com", "555-0199", "ACC-SELLER-99");
        seller.setRating(4.9);
        seller.setTotalListings(3);

        // Default Demo Bidder
        Bidder bidder = new Bidder("USR-1003", "alice_bidder", "bidder123", "Alice Smith", "alice@biddermail.com", "555-0144", 2500.0, "742 Evergreen Terrace");

        userCache.put(admin.getId(), admin);
        userCache.put(seller.getId(), seller);
        userCache.put(bidder.getId(), bidder);

        persistData();
    }

    private synchronized void persistData() {
        try {
            FileStorageManager.saveToFile(STORAGE_FILE, new HashMap<>(userCache));
        } catch (IOException e) {
            System.err.println("[UserRepository] Error persisting user data: " + e.getMessage());
        }
    }

    @Override
    public synchronized User save(User user) {
        userCache.put(user.getId(), user);
        persistData();
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(userCache.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userCache.values());
    }

    @Override
    public synchronized boolean deleteById(String id) {
        if (userCache.containsKey(id)) {
            userCache.remove(id);
            persistData();
            return true;
        }
        return false;
    }

    @Override
    public boolean existsById(String id) {
        return id != null && userCache.containsKey(id);
    }

    @Override
    public long count() {
        return userCache.size();
    }

    public Optional<User> findByUsername(String username) {
        if (username == null) return Optional.empty();
        return userCache.values().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username.trim()))
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        if (email == null) return false;
        return userCache.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email.trim()));
    }

    public List<User> findByRole(UserRole role) {
        List<User> filtered = new ArrayList<>();
        for (User u : userCache.values()) {
            if (u.getRole() == role) {
                filtered.add(u);
            }
        }
        return Collections.unmodifiableList(filtered);
    }
}
