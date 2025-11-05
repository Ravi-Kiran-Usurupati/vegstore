package com.vegstore.controller;

import com.vegstore.entity.*;
import com.vegstore.repository.UserRepository;
import com.vegstore.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final ProductService productService;
    private final SupplierService supplierService;
    private final PurchaseService purchaseService;
    private final OrderService orderService;
    private final UserService userService;
    private final AdminService adminService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("totalProducts", productService.getAllProducts().size());
        model.addAttribute("totalSuppliers", supplierService.getAllSuppliers().size());
        model.addAttribute("pendingOrders", orderService.getPendingOrders().size());
        model.addAttribute("totalUsers", userService.getAllUsers().size());
        model.addAttribute("currentUser", currentUser);

        return "admin/dashboard";
    }

     @GetMapping("/users")
     public String usersManagement(Model model, @AuthenticationPrincipal User currentUser) {
        try {
        log.info("Starting users management endpoint...");

        List<User> users = userRepository.findAll();
        log.info("Found {} users", users.size());

        Map<String, Long> userStats = new HashMap<>();
        userStats.put("totalUsers", userRepository.count());
        userStats.put("customers", userRepository.countByRole(User.Role.CUSTOMER));
        userStats.put("salespersons", userRepository.countByRole(User.Role.SALESPERSON));
        userStats.put("admins", userRepository.countByRole(User.Role.ADMIN));

        model.addAttribute("users", users);
        model.addAttribute("userStats", userStats);
        model.addAttribute("currentUser", currentUser);

        log.info("Users management endpoint completed successfully");
        return "admin/admin-users";

        } catch (Exception e) {
        log.error("Error in users management: ", e);
        model.addAttribute("error", "Error loading users: " + e.getMessage());
        return "admin/admin-users";
    }
}

    @PostMapping("/users/create")
    public String createUser(@RequestParam String fullName,
                             @RequestParam String username,
                             @RequestParam String password,
                             @RequestParam User.Role role,
                             @RequestParam(required = false) Boolean isWholesale,
                             RedirectAttributes redirectAttributes) {

        // Check if username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Username already exists");
            return "redirect:/admin/users";
        }

        try {
            User user = User.builder()
                    .fullName(fullName)
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .isWholesale(isWholesale != null ? isWholesale : false)
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "User created successfully");
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error creating user: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    // ========== PRODUCTS ==========

    @GetMapping("/products")
    public String products(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("product", new Product());
        model.addAttribute("currentUser", currentUser);
        return "admin/products";
    }

    @PostMapping("/products/add")
    public String addProduct(@Valid @ModelAttribute Product product,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation failed");
            return "redirect:/admin/products";
        }

        try {
            productService.createProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product added successfully");
        } catch (Exception e) {
            log.error("Error adding product: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error adding product: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute Product product,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation failed");
            return "redirect:/admin/products";
        }

        try {
            product.setId(id);
            productService.updateProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully");
        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error updating product: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Product> optionalProduct = productService.getproductById(id);
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                product.setStockKg(0.0); // Soft delete by emptying stock
                // Do *not* change "active"
                productService.updateProduct(product);
                redirectAttributes.addFlashAttribute("success", "Product stock set to 0 (soft-deleted)");
            } else {
                redirectAttributes.addFlashAttribute("error", "Product not found");
            }
        } catch (Exception e) {
            log.error("Error soft-deleting product: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error soft-deleting product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    // REST endpoint to fetch product for edit (used by AJAX)
    @GetMapping("/products/{id}")
    @ResponseBody
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        Optional<Product> optionalProduct = productService.getproductById(id);
        if (optionalProduct.isPresent()) {
            return ResponseEntity.ok(optionalProduct.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
    }

    // ========== SUPPLIERS ==========

    @GetMapping("/suppliers")
    public String suppliers(Model model, @AuthenticationPrincipal User currentUser) {
        try {
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            model.addAttribute("supplier", new Supplier());
            model.addAttribute("currentUser", currentUser);
            return "admin/suppliers";
        } catch (Exception e) {
            log.error("Error loading suppliers: {}", e.getMessage());
            model.addAttribute("error", "Error loading suppliers: " + e.getMessage());
            return "admin/suppliers";
        }
    }

    @PostMapping("/suppliers/add")
    public String addSupplier(@ModelAttribute Supplier supplier,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {

        log.info("Attempting to add supplier: {}", supplier.getName());

        if (result.hasErrors()) {
            log.error("Validation errors: {}", result.getAllErrors());
            redirectAttributes.addFlashAttribute("error", "Validation failed");
            return "redirect:/admin/suppliers";
        }

        try {
            Supplier savedSupplier = supplierService.createSupplier(supplier);
            log.info("Supplier created successfully with ID: {}", savedSupplier.getId());
            redirectAttributes.addFlashAttribute("success", "Supplier added successfully!");
        } catch (Exception e) {
            log.error("Error adding supplier: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error adding supplier: " + e.getMessage());
        }

        return "redirect:/admin/suppliers";
    }

    @PostMapping("/suppliers/update/{id}")
    public String updateSupplier(@PathVariable Long id,
                                 @ModelAttribute Supplier supplier,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {

        log.info("Attempting to update supplier ID: {}", id);

        if (result.hasErrors()) {
            log.error("Validation errors: {}", result.getAllErrors());
            redirectAttributes.addFlashAttribute("error", "Validation failed");
            return "redirect:/admin/suppliers";
        }

        try {
            supplierService.updateSupplier(id, supplier);
            redirectAttributes.addFlashAttribute("success", "Supplier updated successfully!");
        } catch (Exception e) {
            log.error("Error updating supplier: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error updating supplier: " + e.getMessage());
        }

        return "redirect:/admin/suppliers";
    }

    @PostMapping("/suppliers/delete/{id}")
    public String deleteSupplier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Attempting to delete supplier ID: {}", id);

        try {
            supplierService.deleteSupplier(id);
            redirectAttributes.addFlashAttribute("success", "Supplier deleted successfully!");
        } catch (Exception e) {
            log.error("Error deleting supplier: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Cannot delete supplier. It may be associated with products.");
        }

        return "redirect:/admin/suppliers";
    }


    // ========== PURCHASES ==========

    @GetMapping("/purchases")
    public String purchases(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("purchases", purchaseService.getAllPurchases());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("purchase", new Purchase());
        model.addAttribute("currentUser", currentUser);
        return "admin/purchases";
    }

    @PostMapping("/purchases/add")
    public String addPurchase(@Valid @ModelAttribute Purchase purchase,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation failed");
            return "redirect:/admin/purchases";
        }

        try {
            purchaseService.createPurchase(purchase);
            redirectAttributes.addFlashAttribute("success", "Purchase recorded successfully and stock updated");
        } catch (Exception e) {
            log.error("Error adding purchase: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error adding purchase: " + e.getMessage());
        }

        return "redirect:/admin/purchases";
    }

    @PostMapping("/purchases/delete/{id}")
    public String deletePurchase(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            purchaseService.deletePurchase(id);
            redirectAttributes.addFlashAttribute("success", "Purchase deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting purchase: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error deleting purchase: " + e.getMessage());
        }

        return "redirect:/admin/purchases";
    }

    // ========== API ENDPOINTS FOR CHARTS ==========

    @GetMapping("/api/sales-trend")
    @ResponseBody
    public Map<String, BigDecimal> getSalesTrend(@RequestParam(defaultValue = "30") int days) {
        return adminService.getSalesTrend(days);
    }

    @GetMapping("/api/salesperson-performance")
    @ResponseBody
    public Map<String, Long> getSalespersonPerformance() {
        return adminService.getSalespersonPerformance();
    }

    @GetMapping("/api/profit-analysis")
    @ResponseBody
    public Map<String, BigDecimal> getProfitAnalysis() {
        return adminService.getProfitAnalysis();
    }

    @GetMapping("/users/{id}/details")
    @ResponseBody
    public Map<String, Object> getUserDetails(@PathVariable Long id) {
        Map<String, Object> userDetails = new HashMap<>();

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            userDetails.put("id", user.getId());
            userDetails.put("fullName", user.getFullName());
            userDetails.put("username", user.getUsername());
            userDetails.put("role", user.getRole().name());
            userDetails.put("wholesale", user.isWholesale());
            userDetails.put("createdAt", user.getCreatedAt());
        } else {
            userDetails.put("error", "User not found");
        }

        return userDetails;
    }

    @PostMapping("/users/update")
    public String updateUser(@RequestParam Long id,
                             @RequestParam String fullName,
                             @RequestParam String username,
                             @RequestParam(required = false) String password,
                             @RequestParam User.Role role,
                             @RequestParam(required = false) Boolean isWholesale,
                             RedirectAttributes redirectAttributes) {

        try {
            Optional<User> userOpt = userRepository.findById(id);
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "User not found");
                return "redirect:/admin/users";
            }

            User user = userOpt.get();

            // Check if username is being changed and if new username already exists
            if (!user.getUsername().equals(username) && userRepository.findByUsername(username).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Username already exists");
                return "redirect:/admin/users";
            }

            user.setFullName(fullName);
            user.setUsername(username);
            user.setRole(role);

            // Use the proper setter method
            user.setIsWholesale(isWholesale != null ? isWholesale : false);

            // Update password only if provided
            if (password != null && !password.trim().isEmpty()) {
                if (password.length() < 3) {
                    redirectAttributes.addFlashAttribute("error", "Password must be at least 3 characters long");
                    return "redirect:/admin/users";
                }
                user.setPassword(passwordEncoder.encode(password));
            }

            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "User updated successfully");

        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error updating user: " + e.getMessage());
        }

        return "redirect:/admin/users?updated=true";
    }

    @Transactional
    @PostMapping("/users/toggle-status/{id}")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.info("Attempting to TOGGLE status for user with ID: {}", id);

        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // This line toggles the active status
            boolean newStatus = !user.getActive();
            user.setActive(newStatus);

            userRepository.save(user);

            String action = newStatus ? "activated" : "deactivated";
            log.info("User {} successfully {}.", user.getUsername(), action);
            redirectAttributes.addFlashAttribute("success", "User " + user.getUsername() + " " + action + " successfully.");

        } catch (Exception e) {
            log.error("Error toggling user status: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error toggling user status: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }
}