//package com.vegstore.controller;
//
//import com.vegstore.entity.Order;
//import com.vegstore.entity.User;
//import com.vegstore.service.OrderService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//import java.util.Map;
//
//@Controller
//@RequiredArgsConstructor
//@Slf4j
//public class CustomerController {
//
//    private final OrderService orderService;
//
//
//
//    @PostMapping("/checkout")
//    @ResponseBody
//    public Map<String, Object> processCheckout(@RequestBody Map<Long, Double> cartItems,
//                                               Model model) {
//        try {
//            User currentUser = (User) model.getAttribute("currentUser");
//            Order order = orderService.createOrder(currentUser, cartItems);
//
//            return Map.of(
//                    "success", true,
//                    "orderId", order.getId(),
//                    "message", "Order placed successfully!"
//            );
//        } catch (Exception e) {
//            log.error("Checkout error: {}", e.getMessage());
//            return Map.of(
//                    "success", false,
//                    "message", e.getMessage()
//            );
//        }
//    }
//
//    @GetMapping("/my-orders")
//    public String myOrders(Model model) {
//        User currentUser = (User) model.getAttribute("currentUser");
//        List<Order> orders = orderService.getCustomerOrders(currentUser);
//        model.addAttribute("orders", orders);
//        return "my-orders";
//    }
//}
package com.vegstore.controller;

import com.vegstore.entity.Order;
import com.vegstore.entity.OrderItem;
import com.vegstore.entity.Product;
import com.vegstore.entity.User;
import com.vegstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final OrderService orderService;

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

    // START OF MODIFIED METHOD WITH DUMMY DATA
    @GetMapping("/my-orders")
    public String myOrders(Model model) {
        // This is where we create the dummy data instead of calling the database.
        List<Order> dummyOrders = new ArrayList<>();
        User currentUser = (User) model.getAttribute("currentUser"); // Still useful for context
        User salesperson = new User(); // Dummy salesperson
        salesperson.setFullName("John Sales");

        // --- DUMMY ORDER 1: Delivered ---
        Order order1 = new Order();
        order1.setId(101L);
        order1.setStatus(Order.OrderStatus.DELIVERED);
        order1.setCreatedAt(LocalDateTime.now().minusDays(5));
        order1.setTotalAmount(new BigDecimal("185.00"));
        order1.setSalesperson(salesperson);

        Product tomato = Product.builder().name("Tomatoes").build();
        Product potato = Product.builder().name("Potatoes").build();

        OrderItem item1 = new OrderItem(1L, order1, tomato, 2.0, new BigDecimal("45.00")); // 90.00
        OrderItem item2 = new OrderItem(2L, order1, potato, 3.0, new BigDecimal("35.00")); // 95.00
        order1.setOrderItems(List.of(item1, item2));
        dummyOrders.add(order1);

        // --- DUMMY ORDER 2: Processing ---
        Order order2 = new Order();
        order2.setId(102L);
        order2.setStatus(Order.OrderStatus.PROCESSING);
        order2.setCreatedAt(LocalDateTime.now().minusDays(1));
        order2.setTotalAmount(new BigDecimal("50.00"));
        order2.setSalesperson(salesperson);

        Product spinach = Product.builder().name("Spinach (Palak)").build();
        OrderItem item3 = new OrderItem(3L, order2, spinach, 1.0, new BigDecimal("50.00"));
        order2.setOrderItems(List.of(item3));
        dummyOrders.add(order2);

        // --- DUMMY ORDER 3: Pending (no salesperson yet) ---
        Order order3 = new Order();
        order3.setId(103L);
        order3.setStatus(Order.OrderStatus.PENDING);
        order3.setCreatedAt(LocalDateTime.now().minusHours(2));
        order3.setTotalAmount(new BigDecimal("65.00"));
        order3.setSalesperson(null); // No salesperson assigned yet

        Product beans = Product.builder().name("Green Beans").build();
        OrderItem item4 = new OrderItem(4L, order3, beans, 1.0, new BigDecimal("65.00"));
        order3.setOrderItems(List.of(item4));
        dummyOrders.add(order3);

        // Add the list of dummy orders to the model
        model.addAttribute("orders", dummyOrders);

        return "my-orders";
    }
    // END OF MODIFIED METHOD
}