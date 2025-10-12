package com.vegstore.service;

import com.vegstore.entity.*;
import com.vegstore.repository.OrderRepository;
import com.vegstore.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    private final ProductService productService;

    private final PurchaseRepository purchaseRepository;

    @Transactional
    public Order createOrder(User customer, Map<Long, Double> cartItems) {
        log.info("Creating order for customer: {}", customer.getUsername());

        Order order = Order.builder()
                .customer(customer)
                .status(Order.OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Map.Entry<Long, Double> entry : cartItems.entrySet()) {
            Long productId = entry.getKey();
            Double quantity = entry.getValue();

            Product product = productService.getProductById(productId);

            if (product.getStockKg() < quantity) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            BigDecimal pricePerKg = product.getPriceForCustomer(
                    customer.getIsWholesale(),
                    quantity
            );

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantityKg(quantity)
                    .pricePerKgAtTimeOfOrder(pricePerKg)
                    .build();

            order.addOrderItem(orderItem);

            BigDecimal itemSubtotal = pricePerKg.multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(itemSubtotal);

            productService.decreaseStock(productId, quantity);

            log.info("Added order item - Product: {}, Quantity: {} kg, Price: {} per kg",
                    product.getName(), quantity, pricePerKg);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        log.info("Order created successfully with ID: {} and total amount: {}",
                savedOrder.getId(), totalAmount);

        return savedOrder;
    }

    public List<Order> getCustomerOrders(User customer) {
        return orderRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }

    public List<Order> getSalespersonOrders(User salesperson) {
        return orderRepository.findBySalespersonOrderByCreatedAtDesc(salesperson);
    }

    public List<Order> getPendingOrders() {
        return orderRepository.findByStatusOrderByCreatedAtDesc(Order.OrderStatus.PENDING);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Transactional
    public Order claimOrder(Long orderId, User salesperson) {
        Order order = getOrderById(orderId);

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Order is not available for claiming");
        }

        order.setSalesperson(salesperson);
        order.setStatus(Order.OrderStatus.PROCESSING);

        log.info("Order {} claimed by salesperson: {}", orderId, salesperson.getUsername());
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);

        log.info("Order {} status updated to: {}", orderId, status);
        return orderRepository.save(order);
    }

    public List<Order> getOrdersForLastNDays(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return orderRepository.findOrdersAfterDate(startDate);
    }

    public BigDecimal calculateOrderItemProfit(OrderItem orderItem) {
        BigDecimal purchaseCost = purchaseRepository.findLatestPurchaseForProduct(orderItem.getProduct())
                .map(Purchase::getCostPerKg)
                .orElse(BigDecimal.ZERO);

        BigDecimal salePrice = orderItem.getPricePerKgAtTimeOfOrder();
        BigDecimal profitPerKg = salePrice.subtract(purchaseCost);

        return profitPerKg.multiply(BigDecimal.valueOf(orderItem.getQuantityKg()));
    }
}
