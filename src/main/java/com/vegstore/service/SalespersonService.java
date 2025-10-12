package com.vegstore.service;

import com.vegstore.entity.Order;
import com.vegstore.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalespersonService {

    private final OrderService orderService;

    public List<Order> getAvailableOrders() {
        return orderService.getPendingOrders();
    }

    public List<Order> getMySalesOrders(User salesperson) {
        return orderService.getSalespersonOrders(salesperson);
    }

    public Order claimOrder(Long orderId, User salesperson) {
        return orderService.claimOrder(orderId, salesperson);
    }

    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        return orderService.updateOrderStatus(orderId, status);
    }
}
