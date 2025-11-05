package com.vegstore.config;

import com.vegstore.entity.User;
import com.vegstore.entity.Supplier;
import com.vegstore.entity.Product;
import com.vegstore.entity.Purchase;
import com.vegstore.repository.UserRepository;
import com.vegstore.repository.SupplierRepository;
import com.vegstore.repository.ProductRepository;
import com.vegstore.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CreateAdminUser {

    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            log.info("Starting Database Initialization...");

            // Check if admin exists to avoid recreating data
            if (userRepository.findByUsername("admin").isEmpty()) {
                // Create Users (Admin, Sales, Customers)
                createUsers();

                // Create Suppliers
                createSuppliers();

                // Create Products
                createProducts();

                // Create Purchase Data for COGS calculations
                createPurchaseData();

                log.info("Database Initialization Complete!");
                printLoginCredentials();
            } else {
                log.info("Admin user already exists. Skipping data initialization.");
            }
        };
    }

    private void createUsers() {
        log.info("Creating users...");

        String adminPassword = passwordEncoder.encode("admin123");
        String salesPassword = passwordEncoder.encode("sales123");
        String customerPassword = passwordEncoder.encode("password123");

        // Admin User
        User admin = User.builder()
                .username("admin")
                .password(adminPassword)
                .fullName("Admin User")
                .role(User.Role.ADMIN)
                .isWholesale(false)
                .build();
        userRepository.save(admin);
        log.info(" Created: admin");

        // Salesperson
        User sales1 = User.builder()
                .username("sales1")
                .password(salesPassword)
                .fullName("Sales Person 1")
                .role(User.Role.SALESPERSON)
                .isWholesale(false)
                .build();
        userRepository.save(sales1);
        log.info(" Created: sales1");

        User sales2 = User.builder()
                .username("sales2")
                .password(salesPassword)
                .fullName("Sales Person 2")
                .role(User.Role.SALESPERSON)
                .isWholesale(false)
                .build();
        userRepository.save(sales2);
        log.info(" Created: sales2");

        // Retail Customers
        User ravi = User.builder()
                .username("ravi")
                .password(customerPassword)
                .fullName("Ravi Kiran")
                .role(User.Role.CUSTOMER)
                .isWholesale(false)
                .build();
        userRepository.save(ravi);
        log.info(" Created: ravi (Retail Customer)");

        User jaya = User.builder()
                .username("jaya")
                .password(customerPassword)
                .fullName("Jaya Charan")
                .role(User.Role.CUSTOMER)
                .isWholesale(false)
                .build();
        userRepository.save(jaya);
        log.info(" Created: jaya (Retail Customer)");

        // Wholesale Customers
        User wholesale1 = User.builder()
                .username("wholesale1")
                .password(customerPassword)
                .fullName("Hotel Phoniex Wholesale")
                .role(User.Role.CUSTOMER)
                .isWholesale(true)
                .build();
        userRepository.save(wholesale1);

        User wholesale2 = User.builder()
                .username("wholesale2")
                .password(customerPassword)
                .fullName("Jaya Mess Wholesale")
                .role(User.Role.CUSTOMER)
                .isWholesale(true)
                .build();
        userRepository.save(wholesale2);
        log.info(" Created: wholesale customers");

        log.info("Total users created: {}", userRepository.count());
    }

    private void createSuppliers() {
        log.info("Creating suppliers from Vijayawada, AP...");

        // Supplier 1 - Ganesh
        Supplier ganesh = Supplier.builder()
                .name("Ganesh Fresh Vegetables")
                .contactPerson("Ganesh")
                .phone("9876543210")
                .email("ganesh@freshveggies.com")
                .address("Vijayawada, Andhra Pradesh - 520010")
                .build();
        supplierRepository.save(ganesh);
        log.info(" Created supplier: Ganesh Fresh Vegetables");

        // Supplier 2 - Koti
        Supplier koti = Supplier.builder()
                .name("Koti Organic Farms")
                .contactPerson("Koti")
                .phone("9876543211")
                .email("koti@organicfarms.com")
                .address("Vijayawada, Andhra Pradesh - 520004")
                .build();
        supplierRepository.save(koti);
        log.info(" Created supplier: Koti Organic Farms");

        // Supplier 3 - Swaroop
        Supplier swaroop = Supplier.builder()
                .name("Swaroop Vegetable Suppliers")
                .contactPerson("Swaroop ")
                .phone("9876543212")
                .email("swaroop@vegsuppliers.com")
                .address(" Vijayawada, Andhra Pradesh - 520012")
                .build();
        supplierRepository.save(swaroop);
        log.info(" Created supplier: Swaroop Vegetable Suppliers");

        log.info("Total suppliers created: {}", supplierRepository.count());
    }

    private void createProducts() {
        log.info("Creating products...");

        // Get suppliers
        Supplier ganesh = supplierRepository.findById(1L).orElse(null);
        Supplier koti = supplierRepository.findById(2L).orElse(null);
        Supplier swaroop = supplierRepository.findById(3L).orElse(null);

        // Products from Ganesh Fresh Vegetables
        Product tomato = Product.builder()
                .name("Tomatoes")
                .description("Fresh red tomatoes, locally sourced from Vijayawada farms")
                .category("Fruiting")
                .retailPricePerKg(new BigDecimal("45.00"))
                .wholesalePricePerKg(new BigDecimal("38.00"))
                .minWholesaleQuantityKg(10.0)
                .stockKg(150.0)
                .imageUrl("https://images.unsplash.com/photo-1546094096-0df4bcaaa337?w=400")
                .supplier(ganesh)
                .build();
        productRepository.save(tomato);

        Product potato = Product.builder()
                .name("Potatoes")
                .description("Farm fresh potatoes, perfect for all dishes")
                .category("Root")
                .retailPricePerKg(new BigDecimal("35.00"))
                .wholesalePricePerKg(new BigDecimal("28.00"))
                .minWholesaleQuantityKg(20.0)
                .stockKg(200.0)
                .imageUrl("https://images.unsplash.com/photo-1518977676601-b53f82aba655?w=400")
                .supplier(ganesh)
                .build();
        productRepository.save(potato);

        Product onion = Product.builder()
                .name("Onions")
                .description("Red onions from local farms")
                .category("Root")
                .retailPricePerKg(new BigDecimal("40.00"))
                .wholesalePricePerKg(new BigDecimal("35.00"))
                .minWholesaleQuantityKg(15.0)
                .stockKg(180.0)
                .imageUrl("https://images.unsplash.com/photo-1508747703725-719777637510?w=400")
                .supplier(ganesh)
                .build();
        productRepository.save(onion);

        // Products from Koti Organic Farms
        Product spinach = Product.builder()
                .name("Spinach (Palak)")
                .description("100% organic spinach, rich in iron and nutrients")
                .category("Leafy")
                .retailPricePerKg(new BigDecimal("50.00"))
                .wholesalePricePerKg(new BigDecimal("42.00"))
                .minWholesaleQuantityKg(5.0)
                .stockKg(80.0)
                .imageUrl("https://images.unsplash.com/photo-1576045057995-568f588f82fb?w=400")
                .supplier(koti)
                .build();
        productRepository.save(spinach);

        // Products from Swaroop Vegetable Suppliers
        Product capsicum = Product.builder()
                .name("Capsicum (Shimla Mirchi)")
                .description("Colorful bell peppers, fresh and crunchy")
                .category("Fruiting")
                .retailPricePerKg(new BigDecimal("70.00"))
                .wholesalePricePerKg(new BigDecimal("60.00"))
                .minWholesaleQuantityKg(5.0)
                .stockKg(90.0)
                .imageUrl("https://images.unsplash.com/photo-1563565375-f3fdfdbefa83?w=400")
                .supplier(swaroop)
                .build();
        productRepository.save(capsicum);

        Product carrot = Product.builder()
                .name("Carrots")
                .description("Sweet orange carrots, rich in Vitamin A")
                .category("Root")
                .retailPricePerKg(new BigDecimal("55.00"))
                .wholesalePricePerKg(new BigDecimal("45.00"))
                .minWholesaleQuantityKg(10.0)
                .stockKg(130.0)
                .imageUrl("https://images.unsplash.com/photo-1598170845058-32b9d6a5da37?w=400")
                .supplier(swaroop)
                .build();
        productRepository.save(carrot);

        Product cabbage = Product.builder()
                .name("Cabbage (Gobi)")
                .description("Fresh green cabbage")
                .category("Leafy")
                .retailPricePerKg(new BigDecimal("35.00"))
                .wholesalePricePerKg(new BigDecimal("28.00"))
                .minWholesaleQuantityKg(15.0)
                .stockKg(140.0)
                .imageUrl("https://images.unsplash.com/photo-1594282486552-05b4d80fbb9f?w=400")
                .supplier(swaroop)
                .build();
        productRepository.save(cabbage);

        Product cucumber = Product.builder()
                .name("Cucumber (Dosakaya)")
                .description("Fresh green cucumbers, perfect for salads")
                .category("Fruiting")
                .retailPricePerKg(new BigDecimal("42.00"))
                .wholesalePricePerKg(new BigDecimal("35.00"))
                .minWholesaleQuantityKg(10.0)
                .stockKg(110.0)
                .imageUrl("https://images.unsplash.com/photo-1604977042946-1eecc30f269e?w=400")
                .supplier(swaroop)
                .build();
        productRepository.save(cucumber);

        log.info(" Created {} products", productRepository.count());
    }

    private void createPurchaseData() {
        log.info("Creating purchase data for COGS calculations...");

        // Get products
        Product tomato = productRepository.findByName("Tomatoes").orElse(null);
        Product potato = productRepository.findByName("Potatoes").orElse(null);
        Product onion = productRepository.findByName("Onions").orElse(null);
        Product spinach = productRepository.findByName("Spinach (Palak)").orElse(null);
        Product capsicum = productRepository.findByName("Capsicum (Shimla Mirchi)").orElse(null);
        Product carrot = productRepository.findByName("Carrots").orElse(null);
        Product cabbage = productRepository.findByName("Cabbage (Gobi)").orElse(null);
        Product cucumber = productRepository.findByName("Cucumber (Dosakaya)").orElse(null);

        // Get suppliers
        Supplier ganesh = supplierRepository.findById(1L).orElse(null);
        Supplier koti = supplierRepository.findById(2L).orElse(null);
        Supplier swaroop = supplierRepository.findById(3L).orElse(null);

        // Create purchase records for ALL products (for COGS calculation)
        if (tomato != null && ganesh != null) {
            Purchase tomatoPurchase = Purchase.builder()
                    .product(tomato)
                    .supplier(ganesh)
                    .quantityKg(150.0)
                    .costPerKg(new BigDecimal("25.00"))
                    .totalAmount(new BigDecimal("3750.00")) // Changed from totalCost to totalAmount
                    .purchaseDate(LocalDateTime.now().minusDays(7))
                    .build();
            purchaseRepository.save(tomatoPurchase);
            log.info(" Created purchase record for Tomatoes");
        }

        if (potato != null && ganesh != null) {
            Purchase potatoPurchase = Purchase.builder()
                    .product(potato)
                    .supplier(ganesh)
                    .quantityKg(200.0)
                    .costPerKg(new BigDecimal("18.00"))
                    .totalAmount(new BigDecimal("3600.00")) // Changed from totalCost to totalAmount
                    .purchaseDate(LocalDateTime.now().minusDays(6))
                    .build();
            purchaseRepository.save(potatoPurchase);
            log.info(" Created purchase record for Potatoes");
        }

        if (onion != null && ganesh != null) {
            Purchase onionPurchase = Purchase.builder()
                    .product(onion)
                    .supplier(ganesh)
                    .quantityKg(180.0)
                    .costPerKg(new BigDecimal("22.00"))
                    .totalAmount(new BigDecimal("3960.00")) // Changed from totalCost to totalAmount
                    .purchaseDate(LocalDateTime.now().minusDays(5))
                    .build();
            purchaseRepository.save(onionPurchase);
            log.info(" Created purchase record for Onions");
        }

        if (spinach != null && koti != null) {
            Purchase spinachPurchase = Purchase.builder()
                    .product(spinach)
                    .supplier(koti)
                    .quantityKg(80.0)
                    .costPerKg(new BigDecimal("30.00"))
                    .totalAmount(new BigDecimal("2400.00")) // Changed from totalCost to totalAmount
                    .purchaseDate(LocalDateTime.now().minusDays(4))
                    .build();
            purchaseRepository.save(spinachPurchase);
            log.info(" Created purchase record for Spinach");
        }

        if (capsicum != null && swaroop != null) {
            Purchase capsicumPurchase = Purchase.builder()
                    .product(capsicum)
                    .supplier(swaroop)
                    .quantityKg(90.0)
                    .costPerKg(new BigDecimal("45.00"))
                    .totalAmount(new BigDecimal("4050.00")) // Changed from totalCost to totalAmount
                    .purchaseDate(LocalDateTime.now().minusDays(3))
                    .build();
            purchaseRepository.save(capsicumPurchase);
            log.info(" Created purchase record for Capsicum");
        }

        if (carrot != null && swaroop != null) {
            Purchase carrotPurchase = Purchase.builder()
                    .product(carrot)
                    .supplier(swaroop)
                    .quantityKg(130.0)
                    .costPerKg(new BigDecimal("30.00"))
                    .totalAmount(new BigDecimal("3900.00")) // Changed from totalCost to totalAmount
                    .purchaseDate(LocalDateTime.now().minusDays(2))
                    .build();
            purchaseRepository.save(carrotPurchase);
            log.info(" Created purchase record for Carrots");
        }

        if (cabbage != null && swaroop != null) {
            Purchase cabbagePurchase = Purchase.builder()
                    .product(cabbage)
                    .supplier(swaroop)
                    .quantityKg(140.0)
                    .costPerKg(new BigDecimal("20.00"))
                    .totalAmount(new BigDecimal("2800.00")) // Changed from totalCost to totalAmount
                    .purchaseDate(LocalDateTime.now().minusDays(1))
                    .build();
            purchaseRepository.save(cabbagePurchase);
            log.info(" Created purchase record for Cabbage");
        }

        if (cucumber != null && swaroop != null) {
            Purchase cucumberPurchase = Purchase.builder()
                    .product(cucumber)
                    .supplier(swaroop)
                    .quantityKg(110.0)
                    .costPerKg(new BigDecimal("25.00"))
                    .totalAmount(new BigDecimal("2750.00")) // Changed from totalCost to totalAmount
                    .purchaseDate(LocalDateTime.now().minusDays(1))
                    .build();
            purchaseRepository.save(cucumberPurchase);
            log.info(" Created purchase record for Cucumber");
        }

        log.info("Created purchase data for all 8 products for COGS calculations");
        log.info("Total purchase records created: {}", purchaseRepository.count());
    }

    private void printLoginCredentials() {
        log.info("               GREENBASKET - LOGIN CREDENTIALS          ");
        log.info("");
        log.info("                                                             ");
        log.info("   LOCATION: Vijayawada, Andhra Pradesh                   ");
        log.info("                                                             ");
        log.info("   ADMIN ACCOUNT                                          ");
        log.info("     Username: admin                                         ");
        log.info("     Password: admin123                                      ");
        log.info("                                                             ");
        log.info("   SALESPERSON ACCOUNT                                    ");
        log.info("     Username: sales1                                        ");
        log.info("     Password: sales123                                      ");
        log.info("                                                             ");
        log.info("   RETAIL CUSTOMERS                                       ");
        log.info("     Username: ravi      | Full Name: Ravi Kiran            ");
        log.info("     Username: jaya      | Full Name: Jaya Charan          ");
        log.info("     Password: password123 (for both)                        ");
        log.info("                                                             ");
        log.info("   WHOLESALE CUSTOMERS                                    ");
        log.info("     Username: wholesale1 | Name: Hotel Phoniex Wholesale       ");
        log.info("    Username: wholesale2 | Name: jaya Mess Wholesale    ");
        log.info("    Password: password123 (for both)                        ");
        log.info("                                                             ");
        log.info("  SUPPLIERS                                               ");
        log.info("     1. Ganesh Fresh Vegetables ( Vijayawada)       ");
        log.info("    2. Koti Organic Farms ( Vijayawada)           ");
        log.info("     3. Swaroop Vegetable Suppliers ( Vijayawada)          ");
        log.info("                                                             ");
        log.info("   PRODUCTS: 8 fresh vegetables         ");
        log.info("   PURCHASE DATA: Added for all 8 products (COGS calculations)");
        log.info("   TOTAL INVESTMENT: ₹27,210 in inventory                   ");
        log.info("                                                            ");

        log.info(" Access the application at: http://localhost:8081");
        log.info("");
    }
}