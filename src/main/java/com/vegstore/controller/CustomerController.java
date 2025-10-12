package com.vegstore.controller;

import com.vegstore.entity.Order;
import com.vegstore.entity.User;
import com.vegstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final OrderService orderService;

    @GetMapping("/checkout")
    public String checkout(Model model) {
        return "checkout";
    }

    @PostMapping("/checkout")
    @ResponseBody
    public Map<String, Object> processCheckout(@RequestBody Map<Long, Double> cartItems,
                                               Model model) {
        try {
            User currentUser = (User) model.getAttribute("currentUser");
            Order order = orderService.createOrder(currentUser, cartItems);

            return Map.of(
                    "success", true,
                    "orderId", order.getId(),
                    "message", "Order placed successfully!"
            );
        } catch (Exception e) {
            log.error("Checkout error: {}", e.getMessage());
            return Map.of(
                    "success", false,
                    "message", e.getMessage()
            );
        }
    }

    @GetMapping("/my-orders")
    public String myOrders(Model model) {
        User currentUser = (User) model.getAttribute("currentUser");
        List<Order> orders = orderService.getCustomerOrders(currentUser);
        model.addAttribute("orders", orders);
        return "my-orders";
    }
}
