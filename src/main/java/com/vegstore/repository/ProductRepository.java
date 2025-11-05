package com.vegstore.repository;

import com.vegstore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByStockKgGreaterThan(Double stock);
    List<Product> findByCategory(String category);
    // In ProductRepository.java
    Optional<Product> findByName(String name);
}
