package com.vegstore.controller;

import com.vegstore.entity.*;
import com.vegstore.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final ProductService productService;
    private final SupplierService supplierService;
    private final PurchaseService purchaseService;
    private final OrderService orderService;
    private final UserService userService;
    private final AdminService adminService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productService.getAllProducts().size());
        model.addAttribute("totalSuppliers", supplierService.getAllSuppliers().size());
        model.addAttribute("pendingOrders", orderService.getPendingOrders().size());
        model.addAttribute("totalUsers", userService.getAllUsers().size());

        return "admin/dashboard";
    }

    // ========== PRODUCTS ==========

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("product", new Product());
        return "admin/products";
    }

    @PostMapping("/products/add")
    public String addProduct(@Valid @ModelAttribute Product product,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation failed");
            return "redirect:/admin/products";
        }

        try {
            productService.createProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product added successfully");
        } catch (Exception e) {
            log.error("Error adding product: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error adding product: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute Product product,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation failed");
            return "redirect:/admin/products";
        }

        try {
            product.setId(id);
            productService.updateProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully");
        } catch (Exception e) {
            log.error("Error updating product: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error updating product: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Product deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting product: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error deleting product: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    // ========== SUPPLIERS ==========

    @GetMapping("/suppliers")
    public String suppliers(Model model) {
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("supplier", new Supplier());
        return "admin/suppliers";
    }

    @PostMapping("/suppliers/add")
    public String addSupplier(@Valid @ModelAttribute Supplier supplier,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation failed");
            return "redirect:/admin/suppliers";
        }

        try {
            supplierService.createSupplier(supplier);
            redirectAttributes.addFlashAttribute("success", "Supplier added successfully");
        } catch (Exception e) {
            log.error("Error adding supplier: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error adding supplier: " + e.getMessage());
        }

        return "redirect:/admin/suppliers";
    }

    @PostMapping("/suppliers/update/{id}")
    public String updateSupplier(@PathVariable Long id,
                                 @Valid @ModelAttribute Supplier supplier,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation failed");
            return "redirect:/admin/suppliers";
        }

        try {
            supplierService.updateSupplier(id, supplier);
            redirectAttributes.addFlashAttribute("success", "Supplier updated successfully");
        } catch (Exception e) {
            log.error("Error updating supplier: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error updating supplier: " + e.getMessage());
        }

        return "redirect:/admin/suppliers";
    }

    @PostMapping("/suppliers/delete/{id}")
    public String deleteSupplier(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            supplierService.deleteSupplier(id);
            redirectAttributes.addFlashAttribute("success", "Supplier deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting supplier: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Cannot delete supplier: " + e.getMessage());
        }

        return "redirect:/admin/suppliers";
    }

    // ========== PURCHASES ==========

    @GetMapping("/purchases")
    public String purchases(Model model) {
        model.addAttribute("purchases", purchaseService.getAllPurchases());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("purchase", new Purchase());
        return "admin/purchases";
    }

    @PostMapping("/purchases/add")
    public String addPurchase(@Valid @ModelAttribute Purchase purchase,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Validation failed");
            return "redirect:/admin/purchases";
        }

        try {
            purchaseService.createPurchase(purchase);
            redirectAttributes.addFlashAttribute("success", "Purchase recorded successfully and stock updated");
        } catch (Exception e) {
            log.error("Error adding purchase: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error adding purchase: " + e.getMessage());
        }

        return "redirect:/admin/purchases";
    }

    @PostMapping("/purchases/delete/{id}")
    public String deletePurchase(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            purchaseService.deletePurchase(id);
            redirectAttributes.addFlashAttribute("success", "Purchase deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting purchase: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error deleting purchase: " + e.getMessage());
        }

        return "redirect:/admin/purchases";
    }

    // ========== API ENDPOINTS FOR CHARTS ==========

    @GetMapping("/api/sales-trend")
    @ResponseBody
    public Map<String, BigDecimal> getSalesTrend(@RequestParam(defaultValue = "30") int days) {
        return adminService.getSalesTrend(days);
    }

    @GetMapping("/api/salesperson-performance")
    @ResponseBody
    public Map<String, Long> getSalespersonPerformance() {
        return adminService.getSalespersonPerformance();
    }

    @GetMapping("/api/profit-analysis")
    @ResponseBody
    public Map<String, BigDecimal> getProfitAnalysis() {
        return adminService.getProfitAnalysis();
    }
}
