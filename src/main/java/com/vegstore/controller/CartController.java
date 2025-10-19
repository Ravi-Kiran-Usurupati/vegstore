package com.vegstore.controller;

import com.vegstore.entity.Cart;
import com.vegstore.entity.User;
import com.vegstore.repository.UserRepository;
import com.vegstore.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/cart")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String cart(Model model) {
        User user = getCurrentUser();
        Cart cart = cartService.getCart(user);
        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cart.getItems());
        return "cart";
    }

    @PostMapping("/cart/add")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseBody
    public Map<String, Object> addToCart(
            @RequestParam Long productId,
            @RequestParam Double quantity) {

        try {
            User user = getCurrentUser();
            log.info("Adding product {} to cart for user {}", productId, user.getUsername());
            Cart cart = cartService.addItemToCart(user, productId, quantity);
            return buildCartResponse(cart, "Item added to cart", true);
        } catch (Exception e) {
            log.error("Error adding to cart: {}", e.getMessage(), e);
            return Map.of("success", false, "message", "Error: " + e.getMessage());
        }
    }

    @PostMapping("/cart/update")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseBody
    public Map<String, Object> updateCartItem(
            @RequestParam Long productId,
            @RequestParam Double quantity) {

        try {
            User user = getCurrentUser();
            log.info("Updating product {} quantity to {} for user {}", productId, quantity, user.getUsername());
            Cart cart = cartService.updateItemQuantity(user, productId, quantity);
            return buildCartResponse(cart, "Cart updated", true);
        } catch (Exception e) {
            log.error("Error updating cart: {}", e.getMessage(), e);
            return Map.of("success", false, "message", "Error: " + e.getMessage());
        }
    }

    @PostMapping("/cart/remove/{productId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseBody
    public Map<String, Object> removeFromCart(@PathVariable Long productId) {

        try {
            User user = getCurrentUser();
            log.info("Removing product {} from cart for user {}", productId, user.getUsername());
            cartService.removeItemFromCart(user, productId);
            Cart cart = cartService.getCart(user);
            return buildCartResponse(cart, "Item removed", true);
        } catch (Exception e) {
            log.error("Error removing from cart: {}", e.getMessage(), e);
            return Map.of("success", false, "message", "Error: " + e.getMessage());
        }
    }

    @PostMapping("/cart/clear")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String clearCart(RedirectAttributes redirectAttributes) {
        try {
            User user = getCurrentUser();
            log.info("Clearing cart for user {}", user.getUsername());
            cartService.clearCart(user);
            redirectAttributes.addFlashAttribute("success", "Cart cleared successfully");
        } catch (Exception e) {
            log.error("Error clearing cart: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error clearing cart: " + e.getMessage());
        }
        return "redirect:/cart";
    }

    @GetMapping("/cart/count")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseBody
    public Map<String, Object> getCartCount() {
        try {
            User user = getCurrentUser();
            Cart cart = cartService.getCart(user);
            return Map.of("success", true, "itemCount", cart.getItems().size());
        } catch (Exception e) {
            return Map.of("success", false, "itemCount", 0);
        }
    }

    @GetMapping("/cart/data")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseBody
    public Map<String, Object> getCartData() {
        try {
            User user = getCurrentUser();
            Cart cart = cartService.getCart(user);
            return buildCartResponse(cart, null, true);
        } catch (Exception e) {
            log.error("Error getting cart data: {}", e.getMessage(), e);
            return Map.of("success", false, "message", "Error loading cart", "items", List.of(), "itemCount", 0);
        }
    }

    private Map<String, Object> buildCartResponse(Cart cart, String message, boolean success) {
        List<Map<String, Object>> items = cart.getItems().stream()
                .map(item -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("id", item.getId());
                    itemMap.put("productId", item.getProduct().getId());
                    itemMap.put("name", item.getProduct().getName());
                    itemMap.put("quantity", item.getQuantity());
                    itemMap.put("price", item.getPriceAtTime());
                    itemMap.put("imageUrl", item.getProduct().getImageUrl());
                    itemMap.put("stockKg", item.getProduct().getStockKg());
                    return itemMap;
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        if (message != null) {
            response.put("message", message);
        }
        response.put("items", items);
        response.put("itemCount", items.size());

        return response;
    }
}
