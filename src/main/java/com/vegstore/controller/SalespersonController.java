package com.vegstore.controller;

import com.vegstore.entity.Order;
import com.vegstore.entity.User;
import com.vegstore.repository.UserRepository; // Import this
import com.vegstore.service.SalespersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Import this
import org.springframework.security.core.Authentication; // Import this
import org.springframework.security.core.context.SecurityContextHolder; // Import this
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Import this

import java.util.List;

@Controller
@RequestMapping("/sales")
@RequiredArgsConstructor
@Slf4j // Add this annotation
public class SalespersonController {

    private final SalespersonService salespersonService;
    private final UserRepository userRepository; // Add this field

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

        model.addAttribute("availableOrders", availableOrders);
        model.addAttribute("myOrders", myOrders);
        model.addAttribute("currentUser", currentUser); // Add user to model for the template

        return "sales/dashboard";
    }

    @PostMapping("/claim-order/{orderId}")
    public String claimOrder(@PathVariable Long orderId, RedirectAttributes redirectAttributes) { // Use RedirectAttributes
        User currentUser = getCurrentUser();

        try {
            salespersonService.claimOrder(orderId, currentUser);
            // This sends the "claimed" attribute to the template
            redirectAttributes.addFlashAttribute("claimed", true);
            return "redirect:/sales/dashboard";
        } catch (Exception e) {
            // This sends the "error" attribute to the template
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/sales/dashboard";
        }
    }

    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Long orderId,
                                    @RequestParam Order.OrderStatus status,
                                    RedirectAttributes redirectAttributes) { // Use RedirectAttributes
        try {
            salespersonService.updateOrderStatus(orderId, status);
            // This sends the "updated" attribute to the template
            redirectAttributes.addFlashAttribute("updated", true);
            return "redirect:/sales/dashboard";
        } catch (Exception e) {
            // This sends the "error" attribute to the template
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/sales/dashboard";
        }
    }
}