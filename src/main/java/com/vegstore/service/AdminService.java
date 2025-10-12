package com.vegstore.service;

import com.vegstore.entity.Order;
import com.vegstore.entity.OrderItem;
import com.vegstore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public Map<String, BigDecimal> getSalesTrend(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Order> orders = orderRepository.findOrdersAfterDate(startDate);

        Map<String, BigDecimal> salesByDate = new TreeMap<>();

        // Initialize all dates with zero
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            salesByDate.put(date.toString(), BigDecimal.ZERO);
        }

        // Aggregate sales by date
        for (Order order : orders) {
            if (order.getStatus() != Order.OrderStatus.CANCELLED) {
                String date = order.getCreatedAt().toLocalDate().toString();
                BigDecimal currentAmount = salesByDate.getOrDefault(date, BigDecimal.ZERO);
                salesByDate.put(date, currentAmount.add(order.getTotalAmount()));
            }
        }

        return salesByDate;
    }

    public Map<String, Long> getSalespersonPerformance() {
        List<Object[]> results = orderRepository.getSalespersonPerformance();

        Map<String, Long> performance = new LinkedHashMap<>();
        for (Object[] result : results) {
            com.vegstore.entity.User salesperson = (com.vegstore.entity.User) result[0];
            Long orderCount = (Long) result[1];
            performance.put(salesperson.getFullName(), orderCount);
        }

        return performance;
    }

    public Map<String, BigDecimal> getProfitAnalysis() {
        List<Order> allOrders = orderRepository.findAll();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCogs = BigDecimal.ZERO;

        for (Order order : allOrders) {
            if (order.getStatus() != Order.OrderStatus.CANCELLED) {
                totalRevenue = totalRevenue.add(order.getTotalAmount());

                for (OrderItem item : order.getOrderItems()) {
                    BigDecimal itemProfit = orderService.calculateOrderItemProfit(item);
                    BigDecimal itemRevenue = item.getSubtotal();
                    BigDecimal itemCogs = itemRevenue.subtract(itemProfit);
                    totalCogs = totalCogs.add(itemCogs);
                }
            }
        }

        BigDecimal profit = totalRevenue.subtract(totalCogs);

        Map<String, BigDecimal> analysis = new LinkedHashMap<>();
        analysis.put("revenue", totalRevenue);
        analysis.put("cogs", totalCogs);
        analysis.put("profit", profit);

        return analysis;
    }
}
