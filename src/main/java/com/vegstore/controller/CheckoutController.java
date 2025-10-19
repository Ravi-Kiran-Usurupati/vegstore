package com.vegstore.controller;

import com.vegstore.entity.Order;
import com.vegstore.entity.User;
import com.vegstore.repository.UserRepository;
import com.vegstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {

    private final UserRepository userRepository;
    private final OrderService orderService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Getting current user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        log.info("Found user: ID={}, Username={}, Role={}", user.getId(), user.getUsername(), user.getRole());
        return user;
    }

    @GetMapping
    public String checkout(Model model) {
        try {
            User user = getCurrentUser();
            model.addAttribute("currentUser", user);
            log.info("Checkout page loaded for user: {}", user.getUsername());
            return "checkout";
        } catch (Exception e) {
            log.error("Error loading checkout: {}", e.getMessage(), e);
            return "redirect:/cart";
        }
    }

    @PostMapping("/place-order")
    public String placeOrder(
            @RequestParam String customerName,
            @RequestParam String customerPhone,
            @RequestParam String deliveryAddress,
            @RequestParam String city,
            @RequestParam String pincode,
            @RequestParam(required = false) String deliveryNotes,
            @RequestParam String paymentMethod,
            @RequestParam String cartData,
            RedirectAttributes redirectAttributes) {

        try {
            User user = getCurrentUser();

            log.info("=== PLACE ORDER START ===");
            log.info("User: {} (ID: {})", user.getUsername(), user.getId());
            log.info("Customer Name: {}", customerName);
            log.info("Phone: {}", customerPhone);
            log.info("Address: {}", deliveryAddress);
            log.info("City: {}", city);
            log.info("Pincode: {}", pincode);
            log.info("Payment Method: {}", paymentMethod);
            log.info("Cart Data Length: {}", cartData != null ? cartData.length() : 0);
            log.info("Cart Data: {}", cartData);

            // Validate cart data
            if (cartData == null || cartData.trim().isEmpty() || cartData.equals("[]")) {
                log.error("Cart data is empty!");
                redirectAttributes.addFlashAttribute("error", "Your cart is empty!");
                return "redirect:/cart";
            }

            // Create the order
            Order order = orderService.createOrder(
                    user, customerName, customerPhone, deliveryAddress,
                    city, pincode, deliveryNotes, paymentMethod, cartData
            );

            log.info("=== ORDER CREATED SUCCESSFULLY ===");
            log.info("Order ID: {}", order.getId());
            log.info("Order Total: ₹{}", order.getTotalAmount());
            log.info("Order Items: {}", order.getOrderItems().size());
            log.info("Order Status: {}", order.getStatus());

            redirectAttributes.addFlashAttribute("success",
                    "Order #" + order.getId() + " placed successfully! Total: ₹" + order.getTotalAmount());

            log.info("Redirecting to /my-orders");
            return "redirect:/my-orders";

        } catch (Exception e) {
            log.error("=== ORDER PLACEMENT FAILED ===");
            log.error("Error: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "Failed to place order: " + e.getMessage());
            return "redirect:/checkout";
        }
    }
}
