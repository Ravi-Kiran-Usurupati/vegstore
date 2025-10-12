package com.vegstore.service;

import com.vegstore.entity.Purchase;
import com.vegstore.repository.PurchaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductService productService;

    public List<Purchase> getAllPurchases() {
        return purchaseRepository.findAll();
    }

    public Purchase getPurchaseById(Long id) {
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found with id: " + id));
    }

    @Transactional
    public Purchase createPurchase(Purchase purchase) {
        log.info("Creating purchase for product: {}", purchase.getProduct().getName());

        // Update product stock
        productService.updateStock(purchase.getProduct().getId(), purchase.getQuantityKg());

        Purchase savedPurchase = purchaseRepository.save(purchase);
        log.info("Purchase created with ID: {}", savedPurchase.getId());

        return savedPurchase;
    }

    @Transactional
    public void deletePurchase(Long id) {
        log.info("Deleting purchase with id: {}", id);
        purchaseRepository.deleteById(id);
    }
}
