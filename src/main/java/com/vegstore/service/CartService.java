package com.vegstore.service;

import com.vegstore.entity.Cart;
import com.vegstore.entity.CartItem;
import com.vegstore.entity.Product;
import com.vegstore.entity.User;
import com.vegstore.repository.CartRepository;
import com.vegstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(cart);
                });
    }

    @Transactional
    public Cart addItemToCart(User user, Long productId, Double quantity) {
        Cart cart = getOrCreateCart(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);

            // Update price based on new quantity
            Double price = user.getIsWholesale() && item.getQuantity() >= product.getMinWholesaleQuantityKg()
                    ? product.getWholesalePricePerKg().doubleValue()
                    : product.getRetailPricePerKg().doubleValue();
            item.setPriceAtTime(price);
        } else {
            // Add new item
            Double price = user.getIsWholesale() && quantity >= product.getMinWholesaleQuantityKg()
                    ? product.getWholesalePricePerKg().doubleValue()
                    : product.getRetailPricePerKg().doubleValue();

            CartItem item = CartItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .priceAtTime(price)
                    .build();

            cart.addItem(item);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateItemQuantity(User user, Long productId, Double quantity) {
        Cart cart = getOrCreateCart(user);

        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    if (quantity <= 0) {
                        cart.removeItem(item);
                    } else {
                        item.setQuantity(quantity);

                        // Update price based on new quantity
                        Product product = item.getProduct();
                        Double price = user.getIsWholesale() && quantity >= product.getMinWholesaleQuantityKg()
                                ? product.getWholesalePricePerKg().doubleValue()
                                : product.getRetailPricePerKg().doubleValue();
                        item.setPriceAtTime(price);
                    }
                });

        return cartRepository.save(cart);
    }

    @Transactional
    public void removeItemFromCart(User user, Long productId) {
        Cart cart = getOrCreateCart(user);

        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(cart::removeItem);

        cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = getOrCreateCart(user);
        cart.clearItems();
        cartRepository.save(cart);
    }

    public Cart getCart(User user) {
        return getOrCreateCart(user);
    }
}
