package com.vegstore.config;

import com.vegstore.entity.User;
import com.vegstore.entity.Supplier;
import com.vegstore.entity.Product;
import com.vegstore.repository.UserRepository;
import com.vegstore.repository.SupplierRepository;
import com.vegstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CreateAdminUser {

    private final UserRepository userRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            log.info("========================================");
            log.info("Starting Database Initialization...");
            log.info("========================================");

            // Create Users (Admin, Sales, Customers)
            createUsers();

            // Create Suppliers
            createSuppliers();

            // Create Products
            createProducts();

            log.info("========================================");
            log.info("Database Initialization Complete!");
            log.info("========================================");
            printLoginCredentials();
        };
    }

    private void createUsers() {
        if (userRepository.count() > 0) {
            log.info("Users already exist, skipping user creation");
            return;
        }

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
        log.info("✅ Created: admin");

        // Salesperson
        User sales1 = User.builder()
                .username("sales1")
                .password(salesPassword)
                .fullName("Sales Person")
                .role(User.Role.SALESPERSON)
                .isWholesale(false)
                .build();
        userRepository.save(sales1);
        log.info("✅ Created: sales1");

        // Retail Customers
        User ravi = User.builder()
                .username("ravi")
                .password(customerPassword)
                .fullName("Ravi Kumar")
                .role(User.Role.CUSTOMER)
                .isWholesale(false)
                .build();
        userRepository.save(ravi);
        log.info("✅ Created: ravi (Retail Customer)");

        User jaya = User.builder()
                .username("jaya")
                .password(customerPassword)
                .fullName("Jaya Lakshmi")
                .role(User.Role.CUSTOMER)
                .isWholesale(false)
                .build();
        userRepository.save(jaya);
        log.info("✅ Created: jaya (Retail Customer)");

        // Wholesale Customers
        User wholesale1 = User.builder()
                .username("wholesale1")
                .password(customerPassword)
                .fullName("Hotel Raj Wholesale")
                .role(User.Role.CUSTOMER)
                .isWholesale(true)
                .build();
        userRepository.save(wholesale1);

        User wholesale2 = User.builder()
                .username("wholesale2")
                .password(customerPassword)
                .fullName("Krishna Mess Wholesale")
                .role(User.Role.CUSTOMER)
                .isWholesale(true)
                .build();
        userRepository.save(wholesale2);
        log.info("✅ Created: wholesale customers");

        log.info("Total users created: {}", userRepository.count());
    }

    private void createSuppliers() {
        if (supplierRepository.count() > 0) {
            log.info("Suppliers already exist, skipping");
            return;
        }

        log.info("Creating suppliers from Vijayawada, AP...");

        // Supplier 1 - Ganesh
        Supplier ganesh = Supplier.builder()
                .name("Ganesh Fresh Vegetables")
                .contactPerson("Ganesh Reddy")
                .phone("9876543210")
                .email("ganesh@freshveggies.com")
                .address("MG Road, Benz Circle, Vijayawada, Andhra Pradesh - 520010")
                .build();
        supplierRepository.save(ganesh);
        log.info("✅ Created supplier: Ganesh Fresh Vegetables");

        // Supplier 2 - Koti
        Supplier koti = Supplier.builder()
                .name("Koti Organic Farms")
                .contactPerson("Koti Vara Prasad")
                .phone("9876543211")
                .email("koti@organicfarms.com")
                .address("Gunadala, Vijayawada, Andhra Pradesh - 520004")
                .build();
        supplierRepository.save(koti);
        log.info("✅ Created supplier: Koti Organic Farms");

        // Supplier 3 - Swaroop
        Supplier swaroop = Supplier.builder()
                .name("Swaroop Vegetable Suppliers")
                .contactPerson("Swaroop Krishna")
                .phone("9876543212")
                .email("swaroop@vegsuppliers.com")
                .address("Bhavanipuram, Vijayawada, Andhra Pradesh - 520012")
                .build();
        supplierRepository.save(swaroop);
        log.info("✅ Created supplier: Swaroop Vegetable Suppliers");

        log.info("Total suppliers created: {}", supplierRepository.count());
    }

    private void createProducts() {
        if (productRepository.count() > 0) {
            log.info("Products already exist, skipping");
            return;
        }

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

        Product coriander = Product.builder()
                .name("Coriander (Kothimeera)")
                .description("Fresh organic coriander leaves")
                .category("Leafy")
                .retailPricePerKg(new BigDecimal("60.00"))
                .wholesalePricePerKg(new BigDecimal("50.00"))
                .minWholesaleQuantityKg(3.0)
                .stockKg(50.0)
                .imageUrl("https://images.unsplash.com/photo-1615485500834-bc10199bc94c?w=400")
                .supplier(koti)
                .build();
        productRepository.save(coriander);

        Product brinjal = Product.builder()
                .name("Brinjal (Vankaya)")
                .description("Fresh purple brinjal, ideal for curries")
                .category("Fruiting")
                .retailPricePerKg(new BigDecimal("38.00"))
                .wholesalePricePerKg(new BigDecimal("32.00"))
                .minWholesaleQuantityKg(10.0)
                .stockKg(120.0)
                .imageUrl("https://images.unsplash.com/photo-1618164436241-4473940d1f5c?w=400")
                .supplier(koti)
                .build();
        productRepository.save(brinjal);

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

        Product beans = Product.builder()
                .name("Green Beans (Chikkudu Kaya)")
                .description("Fresh green beans")
                .category("Beans")
                .retailPricePerKg(new BigDecimal("65.00"))
                .wholesalePricePerKg(new BigDecimal("55.00"))
                .minWholesaleQuantityKg(5.0)
                .stockKg(75.0)
                .imageUrl("https://images.unsplash.com/photo-1587411768638-e146e2664780?w=400")
                .supplier(swaroop)
                .build();
        productRepository.save(beans);

        Product ladyFinger = Product.builder()
                .name("Lady Finger (Bendakaya)")
                .description("Fresh okra/bhindi")
                .category("Fruiting")
                .retailPricePerKg(new BigDecimal("48.00"))
                .wholesalePricePerKg(new BigDecimal("40.00"))
                .minWholesaleQuantityKg(8.0)
                .stockKg(95.0)
                .imageUrl("https://images.unsplash.com/photo-1626775238053-4315516eedc9?w=400")
                .supplier(koti)
                .build();
        productRepository.save(ladyFinger);

        log.info("✅ Created {} products", productRepository.count());
    }

    private void printLoginCredentials() {
        log.info("");
        log.info("╔═════════════════════════════════════════════════════════════╗");
        log.info("║              🌿 GREENBASKET - LOGIN CREDENTIALS 🌿         ║");
        log.info("╠═════════════════════════════════════════════════════════════╣");
        log.info("║                                                             ║");
        log.info("║  📍 LOCATION: Vijayawada, Andhra Pradesh                   ║");
        log.info("║                                                             ║");
        log.info("║  🔑 ADMIN ACCOUNT                                          ║");
        log.info("║     Username: admin                                         ║");
        log.info("║     Password: admin123                                      ║");
        log.info("║                                                             ║");
        log.info("║  🔑 SALESPERSON ACCOUNT                                    ║");
        log.info("║     Username: sales1                                        ║");
        log.info("║     Password: sales123                                      ║");
        log.info("║                                                             ║");
        log.info("║  🔑 RETAIL CUSTOMERS                                       ║");
        log.info("║     Username: ravi      | Full Name: Ravi Kumar            ║");
        log.info("║     Username: jaya      | Full Name: Jaya Lakshmi          ║");
        log.info("║     Password: password123 (for both)                        ║");
        log.info("║                                                             ║");
        log.info("║  🔑 WHOLESALE CUSTOMERS                                    ║");
        log.info("║     Username: wholesale1 | Name: Hotel Raj Wholesale       ║");
        log.info("║     Username: wholesale2 | Name: Krishna Mess Wholesale    ║");
        log.info("║     Password: password123 (for both)                        ║");
        log.info("║                                                             ║");
        log.info("║  🚚 SUPPLIERS                                               ║");
        log.info("║     1. Ganesh Fresh Vegetables (MG Road, Vijayawada)       ║");
        log.info("║     2. Koti Organic Farms (Gunadala, Vijayawada)           ║");
        log.info("║     3. Swaroop Vegetable Suppliers (Bhavanipuram)          ║");
        log.info("║                                                             ║");
        log.info("║  📦 PRODUCTS: 12 fresh vegetables with Indian names        ║");
        log.info("║                                                             ║");
        log.info("╚═════════════════════════════════════════════════════════════╝");
        log.info("");
        log.info(" Access the application at: http://localhost:8081");
        log.info("");
    }
}
