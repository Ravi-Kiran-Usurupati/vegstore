package com.vegstore.repository;

import com.vegstore.entity.Order;
import com.vegstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerOrderByCreatedAtDesc(User customer);
    List<Order> findBySalespersonOrderByCreatedAtDesc(User salesperson);
    List<Order> findByStatusOrderByCreatedAtDesc(Order.OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.createdAt >= :startDate ORDER BY o.createdAt DESC")
    List<Order> findOrdersAfterDate(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT o.salesperson, COUNT(o) FROM Order o WHERE o.salesperson IS NOT NULL AND o.status != 'CANCELLED' GROUP BY o.salesperson")
    List<Object[]> getSalespersonPerformance();
}
