package com.bidzone.menu;

import com.bidzone.exception.AuthenticationException;
import com.bidzone.exception.BidZoneException;
import com.bidzone.model.Admin;
import com.bidzone.model.Auction;
import com.bidzone.model.Bid;
import com.bidzone.model.Bidder;
import com.bidzone.model.PaymentStatus;
import com.bidzone.model.Product;
import com.bidzone.model.ProductCategory;
import com.bidzone.model.Receipt;
import com.bidzone.model.Seller;
import com.bidzone.model.Transaction;
import com.bidzone.model.User;
import com.bidzone.model.UserRole;
import com.bidzone.service.AuthenticationService;
import com.bidzone.service.AuctionService;
import com.bidzone.service.BiddingService;
import com.bidzone.service.PaymentService;
import com.bidzone.service.ProductService;
import com.bidzone.service.ReportService;
import com.bidzone.util.ConsoleTableRenderer;
import com.bidzone.util.DateTimeUtil;
import com.bidzone.util.InputValidator;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Controller handling user navigation, role-based dashboards, and interactive console screens.
 * Demonstrates Separation of Concerns: presentation logic separated from business services.
 */
public class MenuController {
    private final Scanner scanner;
    private final AuthenticationService authService;
    private final ProductService productService;
    private final AuctionService auctionService;
    private final BiddingService biddingService;
    private final PaymentService paymentService;
    private final ReportService reportService;

    public MenuController(Scanner scanner, AuthenticationService authService, ProductService productService,
                          AuctionService auctionService, BiddingService biddingService,
                          PaymentService paymentService, ReportService reportService) {
        this.scanner = scanner;
        this.authService = authService;
        this.productService = productService;
        this.auctionService = auctionService;
        this.biddingService = biddingService;
        this.paymentService = paymentService;
        this.reportService = reportService;
    }

    /**
     * Master Main Menu loop.
     */
    public void start() {
        boolean running = true;
        while (running) {
            ConsoleTableRenderer.renderBanner("BIDZONE - ONLINE AUCTION MANAGEMENT SYSTEM");
            System.out.printf("  Current Virtual Time: %s%n", DateTimeUtil.format(DateTimeUtil.now()));
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("  1. Login to Existing Account");
            System.out.println("  2. Register as a Bidder (Buyer)");
            System.out.println("  3. Register as a Seller");
            System.out.println("  4. Browse Live Auctions (Guest View)");
            System.out.println("  5. Advance Virtual Clock (Demo Fast-Forward)");
            System.out.println("  6. Exit BidZone");
            System.out.println("--------------------------------------------------------------------------------");

            int choice = InputValidator.readInt(scanner, ">> Enter your choice (1-6): ", 1, 6);
            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleBidderRegistration();
                    break;
                case 3:
                    handleSellerRegistration();
                    break;
                case 4:
                    renderActiveAuctionsTable(auctionService.getActiveAuctions());
                    break;
                case 5:
                    handleAdvanceClock();
                    break;
                case 6:
                    running = false;
                    System.out.println(">> Thank you for using BidZone. Data auto-saved safely. Goodbye!");
                    break;
            }
        }
    }

    private void handleLogin() {
        ConsoleTableRenderer.renderBanner("USER LOGIN");
        System.out.println("(Demo Accounts: admin/admin123 | john_seller/seller123 | alice_bidder/bidder123)");
        String username = InputValidator.readString(scanner, "Username: ");
        String password = InputValidator.readString(scanner, "Password: ");

        try {
            User user = authService.login(username, password);
            System.out.printf(">> Login successful! Welcome back, %s (%s).%n", user.getFullName(), user.getRole().getDisplayName());
            routeUserDashboard(user);
        } catch (AuthenticationException e) {
            System.out.println(">> Login Failed: " + e.getMessage());
        }
    }

    private void routeUserDashboard(User user) {
        if (user instanceof Admin) {
            showAdminDashboard((Admin) user);
        } else if (user instanceof Seller) {
            showSellerDashboard((Seller) user);
        } else if (user instanceof Bidder) {
            showBidderDashboard((Bidder) user);
        }
    }

    // ==========================================
    //           ADMIN DASHBOARD
    // ==========================================
    private void showAdminDashboard(Admin admin) {
        boolean inSession = true;
        while (inSession) {
            ConsoleTableRenderer.renderBanner("ADMINISTRATOR DASHBOARD - " + admin.getUsername());
            System.out.println("  1. View System Executive Summary");
            System.out.println("  2. View Category Performance Matrix (2D Array)");
            System.out.println("  3. View Top Bidders Leaderboard");
            System.out.println("  4. View All Registered Users Directory");
            System.out.println("  5. Monitor All Auctions (All Statuses)");
            System.out.println("  6. Cancel an Auction");
            System.out.println("  7. View All Financial Transactions");
            System.out.println("  8. View My Admin Profile");
            System.out.println("  9. Logout");
            System.out.println("--------------------------------------------------------------------------------");

            int choice = InputValidator.readInt(scanner, ">> Select Admin Action (1-9): ", 1, 9);
            switch (choice) {
                case 1:
                    reportService.renderSystemExecutiveSummary();
                    break;
                case 2:
                    reportService.renderCategoryPerformanceMatrix();
                    break;
                case 3:
                    reportService.renderTopBiddersLeaderboard();
                    break;
                case 4:
                    reportService.renderAllUsersReport();
                    break;
                case 5:
                    renderFullAuctionsTable(auctionService.getAllAuctions());
                    break;
                case 6:
                    handleCancelAuction(admin.getId(), true);
                    break;
                case 7:
                    renderTransactionsTable(paymentService.getAllTransactions());
                    break;
                case 8:
                    admin.displayProfile();
                    break;
                case 9:
                    authService.logout();
                    inSession = false;
                    break;
            }
        }
    }

    // ==========================================
    //           SELLER DASHBOARD
    // ==========================================
    private void showSellerDashboard(Seller seller) {
        boolean inSession = true;
        while (inSession) {
            ConsoleTableRenderer.renderBanner("SELLER DASHBOARD - " + seller.getUsername());
            System.out.println("  1. Add New Product to Catalog");
            System.out.println("  2. Launch Auction for a Product");
            System.out.println("  3. View My Listed Products");
            System.out.println("  4. View My Auctions & Activity");
            System.out.println("  5. Inspect Auction Bidding Log");
            System.out.println("  6. Cancel My Auction");
            System.out.println("  7. View My Seller Profile");
            System.out.println("  8. Logout");
            System.out.println("--------------------------------------------------------------------------------");

            int choice = InputValidator.readInt(scanner, ">> Select Seller Action (1-8): ", 1, 8);
            switch (choice) {
                case 1:
                    handleAddProduct(seller);
                    break;
                case 2:
                    handleLaunchAuction(seller);
                    break;
                case 3:
                    renderProductsTable(productService.getProductsBySeller(seller.getId()));
                    break;
                case 4:
                    renderFullAuctionsTable(auctionService.getAuctionsBySeller(seller.getId()));
                    break;
                case 5:
                    handleInspectBids();
                    break;
                case 6:
                    handleCancelAuction(seller.getId(), false);
                    break;
                case 7:
                    seller.displayProfile();
                    break;
                case 8:
                    authService.logout();
                    inSession = false;
                    break;
            }
        }
    }

    // ==========================================
    //           BIDDER DASHBOARD
    // ==========================================
    private void showBidderDashboard(Bidder bidder) {
        boolean inSession = true;
        while (inSession) {
            ConsoleTableRenderer.renderBanner("BIDDER DASHBOARD - " + bidder.getUsername());
            System.out.printf("  Wallet Balance: $%.2f | Won Auctions: %d%n", bidder.getWalletBalance(), bidder.getAuctionsWonCount());
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println("  1. Browse Live Auctions (Place Bids)");
            System.out.println("  2. Search Auctions by Keyword");
            System.out.println("  3. Place a Bid on an Auction");
            System.out.println("  4. View My Participated Auctions & History");
            System.out.println("  5. View Won Auctions & Settle Payment (Checkout)");
            System.out.println("  6. Deposit Funds to Simulated Wallet");
            System.out.println("  7. View My Bidder Profile");
            System.out.println("  8. Logout");
            System.out.println("--------------------------------------------------------------------------------");

            int choice = InputValidator.readInt(scanner, ">> Select Bidder Action (1-8): ", 1, 8);
            switch (choice) {
                case 1:
                    renderActiveAuctionsTable(auctionService.getActiveAuctions());
                    break;
                case 2:
                    handleSearchAuctions();
                    break;
                case 3:
                    handlePlaceBid(bidder);
                    break;
                case 4:
                    renderFullAuctionsTable(biddingService.getParticipatedAuctions(bidder.getId()));
                    break;
                case 5:
                    handleSettleWonAuctions(bidder);
                    break;
                case 6:
                    handleDepositFunds(bidder);
                    break;
                case 7:
                    bidder.displayProfile();
                    break;
                case 8:
                    authService.logout();
                    inSession = false;
                    break;
            }
        }
    }

    // ==========================================
    //           ACTION HANDLERS
    // ==========================================
    private void handleBidderRegistration() {
        ConsoleTableRenderer.renderBanner("NEW BIDDER REGISTRATION");
        String username = InputValidator.readString(scanner, "Choose Username: ");
        String password = InputValidator.readString(scanner, "Choose Password: ");
        String fullName = InputValidator.readString(scanner, "Full Legal Name: ");
        String email = InputValidator.readEmail(scanner, "Email Address: ");
        String phone = InputValidator.readOptionalString(scanner, "Phone Number", "555-0100");
        double initialDeposit = InputValidator.readDouble(scanner, "Initial Wallet Deposit ($): ", 0.0);
        String address = InputValidator.readString(scanner, "Shipping Address: ");

        try {
            Bidder bidder = authService.registerBidder(username, password, fullName, email, phone, initialDeposit, address);
            System.out.printf(">> Registration complete! Bidder ID: %s. You can now login.%n", bidder.getId());
        } catch (AuthenticationException e) {
            System.out.println(">> Registration Error: " + e.getMessage());
        }
    }

    private void handleSellerRegistration() {
        ConsoleTableRenderer.renderBanner("NEW SELLER REGISTRATION");
        String username = InputValidator.readString(scanner, "Choose Username: ");
        String password = InputValidator.readString(scanner, "Choose Password: ");
        String fullName = InputValidator.readString(scanner, "Business / Full Name: ");
        String email = InputValidator.readEmail(scanner, "Email Address: ");
        String phone = InputValidator.readOptionalString(scanner, "Contact Phone", "555-0200");
        String bankRef = InputValidator.readString(scanner, "Payout Bank Account Reference: ");

        try {
            Seller seller = authService.registerSeller(username, password, fullName, email, phone, bankRef);
            System.out.printf(">> Registration complete! Seller ID: %s. You can now login.%n", seller.getId());
        } catch (AuthenticationException e) {
            System.out.println(">> Registration Error: " + e.getMessage());
        }
    }

    private void handleAddProduct(Seller seller) {
        ConsoleTableRenderer.renderBanner("ADD NEW PRODUCT TO CATALOG");
        String title = InputValidator.readString(scanner, "Product Title: ");
        String desc = InputValidator.readString(scanner, "Detailed Description: ");

        System.out.println("Select Category:");
        ProductCategory[] categories = ProductCategory.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("  %d. %s (%s)%n", i + 1, categories[i].name(), categories[i].getDescription());
        }
        int catChoice = InputValidator.readInt(scanner, ">> Choose Category (1-" + categories.length + "): ", 1, categories.length);
        ProductCategory chosenCategory = categories[catChoice - 1];

        String condition = InputValidator.readOptionalString(scanner, "Product Condition", "Brand New");
        double reservePrice = InputValidator.readDouble(scanner, "Reserve Price ($): ", 0.0);

        Product product = productService.createProduct(seller.getId(), seller.getUsername(), title, desc, chosenCategory, condition, reservePrice);
        seller.incrementListings();
        System.out.printf(">> Product [%s] successfully created and saved to catalog!%n", product.getProductId());
    }

    private void handleLaunchAuction(Seller seller) {
        ConsoleTableRenderer.renderBanner("LAUNCH NEW AUCTION");
        List<Product> products = productService.getProductsBySeller(seller.getId());
        if (products.isEmpty()) {
            System.out.println(">> You have no products listed. Please add a product first.");
            return;
        }

        renderProductsTable(products);
        String productId = InputValidator.readString(scanner, ">> Enter Product ID to Auction: ");
        double startPrice = InputValidator.readDouble(scanner, "Starting Price ($): ", 1.0);
        double minInc = InputValidator.readDouble(scanner, "Minimum Bid Increment ($): ", 1.0);
        int durationHours = InputValidator.readInt(scanner, "Auction Duration (Hours): ", 1, 168);

        try {
            Auction auction = auctionService.createAuction(productId, seller.getId(), seller.getUsername(), startPrice, minInc, durationHours);
            System.out.printf(">> Auction [%s] launched successfully! Live until %s.%n", auction.getAuctionId(), DateTimeUtil.format(auction.getEndTime()));
        } catch (Exception e) {
            System.out.println(">> Auction Launch Error: " + e.getMessage());
        }
    }

    private void handlePlaceBid(Bidder bidder) {
        ConsoleTableRenderer.renderBanner("PLACE A BID");
        List<Auction> active = auctionService.getActiveAuctions();
        if (active.isEmpty()) {
            System.out.println(">> No live auctions are currently active.");
            return;
        }
        renderActiveAuctionsTable(active);

        String auctionId = InputValidator.readString(scanner, ">> Enter Auction ID to Bid On: ");
        try {
            Auction auction = auctionService.getAuctionById(auctionId);
            System.out.printf("Item: %s | Current Highest Bid: $%.2f | Minimum Next Bid: $%.2f%n",
                    auction.getProductTitle(), auction.getCurrentHighestBid(), auction.getMinimumNextBid());
            System.out.printf("Your Current Wallet Balance: $%.2f%n", bidder.getWalletBalance());

            double bidAmount = InputValidator.readDouble(scanner, "Enter Your Bid Amount ($): ", auction.getMinimumNextBid());
            Bid bid = biddingService.placeBid(auctionId, bidder, bidAmount);
            System.out.printf(">> SUCCESS! Your bid [%s] of $%.2f is now the HIGHEST BID on %s!%n",
                    bid.getBidId(), bid.getAmount(), auction.getProductTitle());
        } catch (BidZoneException e) {
            System.out.println(">> Bid Rejected: " + e.getMessage());
        }
    }

    private void handleInspectBids() {
        String auctionId = InputValidator.readString(scanner, ">> Enter Auction ID to inspect: ");
        try {
            List<Bid> bids = biddingService.getBiddingHistory(auctionId);
            if (bids.isEmpty()) {
                System.out.println(">> No bids placed on this auction yet.");
                return;
            }

            String[] headers = {"Bid ID", "Bidder Username", "Bid Amount ($)", "Timestamp"};
            String[][] data = new String[bids.size()][4];
            for (int i = 0; i < bids.size(); i++) {
                Bid b = bids.get(i);
                data[i][0] = b.getBidId();
                data[i][1] = b.getBidderUsername();
                data[i][2] = String.format("$%.2f", b.getAmount());
                data[i][3] = DateTimeUtil.formatDetailed(b.getBidTimestamp());
            }
            ConsoleTableRenderer.renderTable("BID AUDIT TRAIL FOR " + auctionId, headers, data);
        } catch (BidZoneException e) {
            System.out.println(">> Error: " + e.getMessage());
        }
    }

    private void handleCancelAuction(String requesterId, boolean isAdmin) {
        String auctionId = InputValidator.readString(scanner, ">> Enter Auction ID to Cancel: ");
        try {
            boolean confirmed = InputValidator.readConfirmation(scanner, "Are you sure you want to cancel auction " + auctionId + "?");
            if (confirmed) {
                boolean cancelled = auctionService.cancelAuction(auctionId, requesterId, isAdmin);
                if (cancelled) {
                    System.out.println(">> Auction " + auctionId + " successfully CANCELLED.");
                } else {
                    System.out.println(">> Unauthorized or unable to cancel auction.");
                }
            }
        } catch (BidZoneException e) {
            System.out.println(">> Error: " + e.getMessage());
        }
    }

    private void handleSearchAuctions() {
        String query = InputValidator.readString(scanner, ">> Enter search keyword (e.g. watch, console): ");
        List<Auction> results = auctionService.searchAuctions(query);
        renderFullAuctionsTable(results);
    }

    private void handleSettleWonAuctions(Bidder bidder) {
        ConsoleTableRenderer.renderBanner("WON AUCTIONS - SETTLEMENT CHECKOUT");
        auctionService.refreshAuctionStatuses();

        List<Auction> won = biddingService.getWonAuctions(bidder.getId());
        if (won.isEmpty()) {
            System.out.println(">> You have no won auctions awaiting settlement.");
            return;
        }

        renderFullAuctionsTable(won);
        String auctionId = InputValidator.readString(scanner, ">> Enter Auction ID to Settle Payment: ");

        Optional<Transaction> txOpt = paymentService.getTransactionByAuctionId(auctionId);
        if (!txOpt.isPresent()) {
            System.out.println(">> No pending transaction found for auction: " + auctionId);
            return;
        }

        Transaction tx = txOpt.get();
        if (tx.getStatus() == PaymentStatus.COMPLETED) {
            System.out.println(">> This auction has already been fully paid and settled!");
            return;
        }

        System.out.printf("Item: %s | Amount Due: $%.2f | Your Balance: $%.2f%n",
                tx.getProductTitle(), tx.getAmount(), bidder.getWalletBalance());

        boolean pay = InputValidator.readConfirmation(scanner, "Confirm payment via In-App Wallet?");
        if (pay) {
            try {
                Receipt receipt = paymentService.settleTransaction(tx.getTransactionId(), bidder);
                System.out.println(">> PAYMENT SUCCESSFUL! Generating official receipt...");
                receipt.printReceipt();
            } catch (BidZoneException e) {
                System.out.println(">> Settlement Error: " + e.getMessage());
            }
        }
    }

    private void handleDepositFunds(Bidder bidder) {
        ConsoleTableRenderer.renderBanner("SIMULATED WALLET DEPOSIT");
        System.out.printf("Current Balance: $%.2f%n", bidder.getWalletBalance());
        double deposit = InputValidator.readDouble(scanner, "Enter Amount to Deposit ($): ", 1.0);
        bidder.depositFunds(deposit);
        authService.getCurrentUser(); // Persisted
        System.out.printf(">> Successfully deposited $%.2f! New Balance: $%.2f%n", deposit, bidder.getWalletBalance());
    }

    private void handleAdvanceClock() {
        ConsoleTableRenderer.renderBanner("DEMONSTRATION SIMULATION CLOCK");
        System.out.println("Fast-forwarding the clock triggers auction status transitions (e.g. ACTIVE -> ENDED)");
        System.out.println("and automatically generates pending transactions for winners without waiting real hours.");
        int hours = InputValidator.readInt(scanner, ">> Enter hours to advance (1 - 720): ", 1, 720);
        DateTimeUtil.advanceClockHours(hours);
        auctionService.refreshAuctionStatuses();
        System.out.println(">> Auction statuses refreshed and synchronized with updated clock.");
    }

    // ==========================================
    //           TABLE RENDER HELPERS
    // ==========================================
    private void renderActiveAuctionsTable(List<Auction> list) {
        String[] headers = {"Auction ID", "Product Title", "Current Highest Bid", "Min Next Bid", "Time Remaining", "Seller"};
        String[][] data = new String[list.size()][6];
        for (int i = 0; i < list.size(); i++) {
            Auction a = list.get(i);
            data[i][0] = a.getAuctionId();
            data[i][1] = a.getProductTitle();
            data[i][2] = a.getCurrentHighestBid() > 0 ? String.format("$%.2f", a.getCurrentHighestBid()) : String.format("$%.2f (Start)", a.getStartingPrice());
            data[i][3] = String.format("$%.2f", a.getMinimumNextBid());
            data[i][4] = DateTimeUtil.getTimeRemaining(a.getEndTime());
            data[i][5] = a.getSellerUsername();
        }
        ConsoleTableRenderer.renderTable("LIVE ACTIVE AUCTIONS", headers, data);
    }

    private void renderFullAuctionsTable(List<Auction> list) {
        String[] headers = {"Auction ID", "Product Title", "Current Bid", "Status", "Winner / Top Bidder", "Paid?", "End Time"};
        String[][] data = new String[list.size()][7];
        for (int i = 0; i < list.size(); i++) {
            Auction a = list.get(i);
            data[i][0] = a.getAuctionId();
            data[i][1] = a.getProductTitle();
            data[i][2] = String.format("$%.2f", a.getCurrentHighestBid() > 0 ? a.getCurrentHighestBid() : a.getStartingPrice());
            data[i][3] = a.getStatus().name();
            data[i][4] = a.getHighestBidderUsername() != null ? a.getHighestBidderUsername() : "None";
            data[i][5] = a.isPaid() ? "YES (Settled)" : (a.getStatus() == com.bidzone.model.AuctionStatus.ENDED ? "PENDING PAYMENT" : "N/A");
            data[i][6] = DateTimeUtil.format(a.getEndTime());
        }
        ConsoleTableRenderer.renderTable("AUCTIONS DIRECTORY", headers, data);
    }

    private void renderProductsTable(List<Product> list) {
        String[] headers = {"Product ID", "Title", "Category", "Condition", "Reserve Price ($)", "Seller"};
        String[][] data = new String[list.size()][6];
        for (int i = 0; i < list.size(); i++) {
            Product p = list.get(i);
            data[i][0] = p.getProductId();
            data[i][1] = p.getTitle();
            data[i][2] = p.getCategory().name();
            data[i][3] = p.getCondition();
            data[i][4] = String.format("$%.2f", p.getReservePrice());
            data[i][5] = p.getSellerUsername();
        }
        ConsoleTableRenderer.renderTable("CATALOG PRODUCTS", headers, data);
    }

    private void renderTransactionsTable(List<Transaction> list) {
        String[] headers = {"Tx ID", "Auction ID", "Product Title", "Buyer", "Amount ($)", "Status", "Settlement Date"};
        String[][] data = new String[list.size()][7];
        for (int i = 0; i < list.size(); i++) {
            Transaction t = list.get(i);
            data[i][0] = t.getTransactionId();
            data[i][1] = t.getAuctionId();
            data[i][2] = t.getProductTitle();
            data[i][3] = t.getBuyerUsername();
            data[i][4] = String.format("$%.2f", t.getAmount());
            data[i][5] = t.getStatus().name();
            data[i][6] = DateTimeUtil.format(t.getTimestamp());
        }
        ConsoleTableRenderer.renderTable("FINANCIAL TRANSACTIONS AUDIT LOG", headers, data);
    }
}
