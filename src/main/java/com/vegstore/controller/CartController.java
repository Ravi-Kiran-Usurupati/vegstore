package com.vegstore.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {

    @GetMapping("/cart")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String cart(Model model) {
        return "cart";
    }
}
