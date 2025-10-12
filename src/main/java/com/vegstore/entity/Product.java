package com.vegstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Retail price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal retailPricePerKg;

    @NotNull(message = "Wholesale price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal wholesalePricePerKg;

    @NotNull(message = "Minimum wholesale quantity is required")
    @Min(value = 1)
    @Column(nullable = false)
    private Double minWholesaleQuantityKg;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0)
    @Column(nullable = false)
    private Double stockKg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    // Category for better UI organization
    @Column(length = 50)
    private String category;

    // Image URL for product display
    @Column(length = 500)
    private String imageUrl;

    public BigDecimal getPriceForCustomer(boolean isWholesale, double quantity) {
        if (isWholesale && quantity >= minWholesaleQuantityKg) {
            return wholesalePricePerKg;
        }
        return retailPricePerKg;
    }
}
