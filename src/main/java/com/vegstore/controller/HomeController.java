package com.vegstore.controller;

import com.vegstore.entity.Product;
import com.vegstore.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    @GetMapping("/")
    public String home(Model model) {
        List<Product> products = productService.getAvailableProducts();
        model.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/products")
    public String products(@RequestParam(required = false) String search, Model model) {
        List<Product> products;

        if (search != null && !search.isEmpty()) {
            products = productService.searchProducts(search);
        } else {
            products = productService.getAllProducts();
        }

        model.addAttribute("products", products);
        model.addAttribute("search", search);
        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "product-detail";
    }



    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
