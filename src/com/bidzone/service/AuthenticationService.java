package com.bidzone.service;

import com.bidzone.exception.AuthenticationException;
import com.bidzone.model.Admin;
import com.bidzone.model.Bidder;
import com.bidzone.model.Seller;
import com.bidzone.model.User;
import com.bidzone.model.UserRole;
import com.bidzone.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Service managing user authentication, registration, and active session state.
 */
public class AuthenticationService {
    private final UserRepository userRepository;
    private User currentUser;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.currentUser = null;
    }

    /**
     * Authenticates credentials and initiates session.
     */
    public User login(String username, String password) throws AuthenticationException {
        if (username == null || password == null) {
            throw new AuthenticationException("Username and password cannot be empty.");
        }

        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new AuthenticationException("No account found with username: " + username);
        }

        User user = optionalUser.get();
        if (!user.isActive()) {
            throw new AuthenticationException("This account has been deactivated. Please contact an Administrator.");
        }

        if (!user.getPassword().equals(password)) {
            throw new AuthenticationException("Invalid password. Please check your credentials.");
        }

        this.currentUser = user;
        return user;
    }

    public void logout() {
        if (currentUser != null) {
            System.out.println(">> User @" + currentUser.getUsername() + " successfully logged out.");
            this.currentUser = null;
        }
    }

    public Bidder registerBidder(String username, String password, String fullName, String email,
                                 String phone, double initialBalance, String shippingAddress) throws AuthenticationException {
        validateUniqueUser(username, email);
        String id = "USR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Bidder bidder = new Bidder(id, username, password, fullName, email, phone, initialBalance, shippingAddress);
        userRepository.save(bidder);
        return bidder;
    }

    public Seller registerSeller(String username, String password, String fullName, String email,
                                 String phone, String bankAccountRef) throws AuthenticationException {
        validateUniqueUser(username, email);
        String id = "USR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Seller seller = new Seller(id, username, password, fullName, email, phone, bankAccountRef);
        userRepository.save(seller);
        return seller;
    }

    public Admin registerAdmin(String username, String password, String fullName, String email,
                               String department) throws AuthenticationException {
        validateUniqueUser(username, email);
        String id = "ADM-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Admin admin = new Admin(id, username, password, fullName, email, department);
        userRepository.save(admin);
        return admin;
    }

    private void validateUniqueUser(String username, String email) throws AuthenticationException {
        if (userRepository.existsByUsername(username)) {
            throw new AuthenticationException("Username '" + username + "' is already taken. Please choose another.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new AuthenticationException("Email '" + email + "' is already registered to an existing account.");
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean hasRole(UserRole role) {
        return currentUser != null && currentUser.getRole() == role;
    }
}
