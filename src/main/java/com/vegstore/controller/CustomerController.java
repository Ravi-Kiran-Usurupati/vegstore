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

import java.util.List;

@Controller
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final UserRepository userRepository;
    private final OrderService orderService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Getting current user for my-orders: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        log.info("Found user: ID={}, Username={}", user.getId(), user.getUsername());
        return user;
    }

    @GetMapping("/my-orders")
    public String myOrders(Model model) {
        try {
            log.info("=== MY ORDERS PAGE LOADING ===");

            User currentUser = getCurrentUser();
            log.info("Loading orders for user: {} (ID: {})", currentUser.getUsername(), currentUser.getId());

            List<Order> orders = orderService.getCustomerOrders(currentUser);
            log.info("Found {} orders for user {}", orders.size(), currentUser.getUsername());

            // Log each order
            for (Order order : orders) {
                log.info("Order #{}: Status={}, Items={}, Total=₹{}, Date={}",
                        order.getId(), order.getStatus(), order.getOrderItems().size(),
                        order.getTotalAmount(), order.getCreatedAt());
            }

            model.addAttribute("currentUser", currentUser);
            model.addAttribute("orders", orders);

            log.info("=== MY ORDERS PAGE READY ===");
            return "my-orders";

        } catch (Exception e) {
            log.error("=== MY ORDERS PAGE FAILED ===");
            log.error("Error: {}", e.getMessage(), e);
            model.addAttribute("error", "Failed to load orders: " + e.getMessage());
            model.addAttribute("orders", List.of());
            return "my-orders";
        }
    }
}

