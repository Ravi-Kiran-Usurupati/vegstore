package com.vegstore.service;

import com.vegstore.entity.*;
import com.vegstore.repository.OrderRepository;
import com.vegstore.repository.ProductRepository;
import com.vegstore.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Add these Jackson imports
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;  // ADDED
    private final ProductService productService;
    private final PurchaseRepository purchaseRepository;
    private final CartService cartService;  // ADDED
    private final ObjectMapper objectMapper;  // ADDED

    @Transactional
    public Order createOrder(User customer, String customerName, String customerPhone,
                             String deliveryAddress, String city, String pincode,
                             String deliveryNotes, String paymentMethod, String cartDataJson) {

        try {
            log.info("=== OrderService.createOrder START ===");
            log.info("Customer: {} (ID: {})", customer.getUsername(), customer.getId());
            log.info("Cart JSON: {}", cartDataJson);

            // Parse cart data
            List<Map<String, Object>> cartItems = objectMapper.readValue(
                    cartDataJson,
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            log.info("Parsed {} cart items", cartItems.size());

            if (cartItems.isEmpty()) {
                throw new RuntimeException("Cart is empty");
            }

            // Create order
            Order order = Order.builder()
                    .customer(customer)
                    .customerName(customerName)
                    .customerPhone(customerPhone)
                    .deliveryAddress(deliveryAddress)
                    .city(city)
                    .pincode(pincode)
                    .deliveryNotes(deliveryNotes)
                    .paymentMethod(paymentMethod)
                    .status(Order.OrderStatus.PENDING)
                    .build();

            log.info("Order object created");

            BigDecimal totalAmount = BigDecimal.ZERO;

            // Create order items
            for (Map<String, Object> cartItem : cartItems) {
                Long productId = ((Number) cartItem.get("productId")).longValue();
                Double quantity = ((Number) cartItem.get("quantity")).doubleValue();
                Double price = ((Number) cartItem.get("price")).doubleValue();

                log.info("Processing item: Product ID={}, Quantity={}, Price={}", productId, quantity, price);

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

                BigDecimal pricePerKg = BigDecimal.valueOf(price);
                BigDecimal subtotal = pricePerKg.multiply(BigDecimal.valueOf(quantity));

                OrderItem orderItem = OrderItem.builder()
                        .product(product)
                        .quantityKg(quantity)
                        .pricePerKgAtTimeOfOrder(pricePerKg)
                        .subtotal(subtotal)
                        .build();

                order.addOrderItem(orderItem);
                totalAmount = totalAmount.add(subtotal);

                // Update product stock
                double newStock = product.getStockKg() - quantity;
                log.info("Updating product stock: {} -> {}", product.getStockKg(), newStock);
                product.setStockKg(newStock);
                productRepository.save(product);
            }

            // Add delivery fee if needed
            if (totalAmount.compareTo(BigDecimal.valueOf(500)) < 0) {
                log.info("Adding delivery fee ₹50 (subtotal < ₹500)");
                totalAmount = totalAmount.add(BigDecimal.valueOf(50));
            }

            order.setTotalAmount(totalAmount);

            // Save order
            log.info("Saving order to database...");
            Order savedOrder = orderRepository.save(order);
            log.info("Order saved with ID: {}", savedOrder.getId());

            // Clear customer's cart
            log.info("Clearing cart for customer: {}", customer.getUsername());
            cartService.clearCart(customer);
            log.info("Cart cleared successfully");

            log.info("=== OrderService.createOrder SUCCESS ===");
            return savedOrder;

        } catch (Exception e) {
            log.error("=== OrderService.createOrder FAILED ===");
            log.error("Error: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create order: " + e.getMessage(), e);
        }
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
