package com.vegstore.service;

import com.vegstore.entity.Product;
import com.vegstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findByStockKgGreaterThan(0.0);
    }

    @Transactional
    public Product createProduct(Product product) {
        log.info("Creating new product: {}", product.getName());
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Product product) {
        Product existing = getProductById(product.getId());
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setRetailPricePerKg(product.getRetailPricePerKg());
        existing.setWholesalePricePerKg(product.getWholesalePricePerKg());
        existing.setMinWholesaleQuantityKg(product.getMinWholesaleQuantityKg());
        existing.setStockKg(product.getStockKg());
        existing.setSupplier(product.getSupplier());
        existing.setCategory(product.getCategory());
        existing.setImageUrl(product.getImageUrl());

        log.info("Updating product: {}", product.getName());
        return productRepository.save(existing);
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    public void updateStock(Long productId, Double quantity) {
        Product product = getProductById(productId);
        product.setStockKg(product.getStockKg() + quantity);
        productRepository.save(product);
    }

    @Transactional
    public void decreaseStock(Long productId, Double quantity) {
        Product product = getProductById(productId);
        if (product.getStockKg() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
        product.setStockKg(product.getStockKg() - quantity);
        productRepository.save(product);
    }
}
