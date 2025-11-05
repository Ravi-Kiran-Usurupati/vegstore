package com.vegstore.controller;

import com.vegstore.entity.Order;
import com.vegstore.entity.User;
import com.vegstore.repository.UserRepository;
import com.vegstore.service.SalespersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/sales")
@RequiredArgsConstructor
@Slf4j
public class SalespersonController {

    private final SalespersonService salespersonService;
    private final UserRepository userRepository;

    /**
     * Helper method to get the currently authenticated user.
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("Getting current user for sales dashboard: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User currentUser = getCurrentUser();
        List<Order> availableOrders = salespersonService.getAvailableOrders();
        List<Order> myOrders = salespersonService.getMySalesOrders(currentUser);

        // Calculate total salary (10% for completed orders assigned to this user)
        BigDecimal salaryAmount = myOrders.stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.COMPLETED)
                .map(order -> order.getTotalAmount().multiply(BigDecimal.valueOf(0.1)))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("availableOrders", availableOrders);
        model.addAttribute("myOrders", myOrders);
        model.addAttribute("salaryAmount", salaryAmount); // <----- Important
        model.addAttribute("currentUser", currentUser);

        return "sales/dashboard";
    }


    @PostMapping("/claim-order/{orderId}")
    public String claimOrder(@PathVariable Long orderId, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();

        try {
            salespersonService.claimOrder(orderId, currentUser);
            redirectAttributes.addFlashAttribute("claimed", true);
        } catch (Exception e) {
            log.error("Error claiming order {}: {}", orderId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Could not claim order: " + e.getMessage());
        }
        return "redirect:/sales/dashboard";
    }

    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Long orderId,
                                    @RequestParam Order.OrderStatus status,
                                    RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();

        try {
            salespersonService.updateOrderStatus(orderId, status, currentUser);
            redirectAttributes.addFlashAttribute("updated", true);
        } catch (Exception e) {
            log.error("Error updating order status for order {}: {}", orderId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Could not update order status: " + e.getMessage());
        }
        return "redirect:/sales/dashboard";
    }
}
