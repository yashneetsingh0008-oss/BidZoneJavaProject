package com.bidzone.main;

import com.bidzone.menu.MenuController;
import com.bidzone.repository.AuctionRepository;
import com.bidzone.repository.ProductRepository;
import com.bidzone.repository.TransactionRepository;
import com.bidzone.repository.UserRepository;
import com.bidzone.service.AuthenticationService;
import com.bidzone.service.AuctionService;
import com.bidzone.service.BiddingService;
import com.bidzone.service.PaymentService;
import com.bidzone.service.ProductService;
import com.bidzone.service.ReportService;

import java.util.Scanner;

/**
 * Main application bootstrap and entry point for the BidZone Online Auction Management System.
 * Demonstrates:
 * - Application Bootstrap & Dependency Wiring (IoC pattern)
 * - Safe JVM Shutdown Hooks for Persistence Guarantee
 * - Console Lifecycle Management
 */
public class BidZoneApp {

    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("               INITIALIZING BIDZONE AUCTION MANAGEMENT SYSTEM                  ");
        System.out.println("================================================================================");

        // 1. Initialize Repositories (loads data from ./data/*.ser automatically)
        UserRepository userRepository = new UserRepository();
        ProductRepository productRepository = new ProductRepository();
        TransactionRepository transactionRepository = new TransactionRepository();
        AuctionRepository auctionRepository = new AuctionRepository();

        // 2. Wire Business Logic Services
        AuthenticationService authService = new AuthenticationService(userRepository);
        ProductService productService = new ProductService(productRepository);
        AuctionService auctionService = new AuctionService(auctionRepository, productRepository, transactionRepository);
        BiddingService biddingService = new BiddingService(auctionRepository, userRepository, auctionService);
        PaymentService paymentService = new PaymentService(transactionRepository, userRepository, auctionRepository);
        ReportService reportService = new ReportService(userRepository, productRepository, auctionRepository, transactionRepository);

        // Initial synchronization of auction statuses
        auctionService.refreshAuctionStatuses();

        // 3. Register JVM Shutdown Hook for guaranteed persistent state flush
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[BidZone Shutdown Hook] Saving all active entities to persistent storage...");
            try {
                auctionRepository.persistData();
                System.out.println("[BidZone Shutdown Hook] Serialization sync complete. Safe shutdown verified.");
            } catch (Exception e) {
                System.err.println("[BidZone Shutdown Hook] Error during shutdown flush: " + e.getMessage());
            }
        }));

        // 4. Launch Presentation Controller
        Scanner scanner = new Scanner(System.in);
        MenuController menuController = new MenuController(
                scanner, authService, productService, auctionService,
                biddingService, paymentService, reportService
        );

        try {
            menuController.start();
        } finally {
            scanner.close();
        }
    }
}
