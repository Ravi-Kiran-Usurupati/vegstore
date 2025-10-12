package com.vegstore.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/checkout")
@PreAuthorize("hasRole('CUSTOMER')")
@Slf4j
public class CheckoutController {

    @GetMapping
    public String checkout(Model model) {
        return "checkout";
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
            log.info("Placing order for customer: {}", customerName);
            log.info("Cart data: {}", cartData);
            log.info("Payment method: {}", paymentMethod);

            // TODO: Process the order here
            // Parse cartData JSON
            // Create Order entity
            // Create OrderItems
            // Save to database
            // Clear cart

            redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
            return "redirect:/my-orders";

        } catch (Exception e) {
            log.error("Error placing order: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Failed to place order: " + e.getMessage());
            return "redirect:/checkout";
        }
    }
}
