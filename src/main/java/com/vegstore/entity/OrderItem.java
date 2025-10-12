package com.vegstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @Min(value = 1)
    @Column(nullable = false)
    private Double quantityKg;

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerKgAtTimeOfOrder;

    public BigDecimal getSubtotal() {
        return pricePerKgAtTimeOfOrder.multiply(BigDecimal.valueOf(quantityKg));
    }
}
