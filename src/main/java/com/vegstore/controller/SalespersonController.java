package com.vegstore.controller;

import com.vegstore.entity.Order;
import com.vegstore.entity.User;
import com.vegstore.service.SalespersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SalespersonController {

    private final SalespersonService salespersonService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User currentUser = (User) model.getAttribute("currentUser");

        List<Order> availableOrders = salespersonService.getAvailableOrders();
        List<Order> myOrders = salespersonService.getMySalesOrders(currentUser);

        model.addAttribute("availableOrders", availableOrders);
        model.addAttribute("myOrders", myOrders);

        return "sales/dashboard";
    }

    @PostMapping("/claim-order/{orderId}")
    public String claimOrder(@PathVariable Long orderId, Model model) {
        User currentUser = (User) model.getAttribute("currentUser");

        try {
            salespersonService.claimOrder(orderId, currentUser);
            return "redirect:/sales/dashboard?claimed";
        } catch (Exception e) {
            return "redirect:/sales/dashboard?error=" + e.getMessage();
        }
    }

    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Long orderId,
                                    @RequestParam Order.OrderStatus status) {
        try {
            salespersonService.updateOrderStatus(orderId, status);
            return "redirect:/sales/dashboard?updated";
        } catch (Exception e) {
            return "redirect:/sales/dashboard?error=" + e.getMessage();
        }
    }
}
