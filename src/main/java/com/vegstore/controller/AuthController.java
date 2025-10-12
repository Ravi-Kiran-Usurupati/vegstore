//package com.vegstore.controller;
//
//import com.vegstore.entity.User;
//import com.vegstore.service.UserService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//@Controller
//@RequiredArgsConstructor
//@Slf4j
//public class AuthController {
//
//    private final UserService userService;
//
//    @GetMapping("/login")
//    public String login() {
//        return "login";
//    }
//
//    @GetMapping("/register")
//    public String showRegistrationForm(Model model) {
//        model.addAttribute("user", new User());
//        return "register";
//    }
//
//    @PostMapping("/register")
//    public String registerUser(@Valid @ModelAttribute("user") User user,
//                               BindingResult result,
//                               Model model) {
//        if (result.hasErrors()) {
//            return "register";
//        }
//
//        try {
//            // Set default role to CUSTOMER
//            user.setRole(User.Role.CUSTOMER);
//            userService.registerUser(user);
//            log.info("User registered successfully: {}", user.getUsername());
//            return "redirect:/login?registered";
//        } catch (Exception e) {
//            log.error("Registration error: {}", e.getMessage());
//            model.addAttribute("error", e.getMessage());
//            return "register";
//        }
//    }
//
//    @GetMapping("/dashboard")
//    public String dashboard(Model model) {
//        User currentUser = (User) model.getAttribute("currentUser");
//
//        if (currentUser == null) {
//            return "redirect:/login";
//        }
//
//        // Redirect based on role
//        return switch (currentUser.getRole()) {
//            case ADMIN -> "redirect:/admin/dashboard";
//            case SALESPERSON -> "redirect:/sales/dashboard";
//            case CUSTOMER -> "redirect:/";
//        };
//    }
//}
package com.vegstore.controller;

import com.vegstore.entity.User;
import com.vegstore.service.UserService;
import com.vegstore.service.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        @RequestParam(required = false) String registered,
                        Model model) {

        if (error != null) {
            log.error("❌ Login failed - displaying error");
            model.addAttribute("error", true);
        }

        if (logout != null) {
            log.info("✅ User logged out");
            model.addAttribute("logout", true);
        }

        if (registered != null) {
            log.info("✅ User registered successfully");
            model.addAttribute("registered", true);
        }

        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               Model model) {

        if (result.hasErrors()) {
            log.error("Validation errors: {}", result.getAllErrors());
            return "register";
        }

        try {
            user.setRole(User.Role.CUSTOMER);
            userService.registerUser(user);
            log.info("✅ User registered: {}", user.getUsername());
            return "redirect:/login?registered";
        } catch (Exception e) {
            log.error("❌ Registration failed: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            log.warn("Unauthenticated access to dashboard");
            return "redirect:/login";
        }

        String username = auth.getName();
        log.info("Dashboard access by: {}", username);

        try {
            User user = userDetailsService.getUserByUsername(username);
            log.info("Redirecting {} to {} dashboard", username, user.getRole());

            return switch (user.getRole()) {
                case ADMIN -> "redirect:/admin/dashboard";
                case SALESPERSON -> "redirect:/sales/dashboard";
                case CUSTOMER -> "redirect:/";
            };
        } catch (Exception e) {
            log.error("Error redirecting user: {}", e.getMessage());
            return "redirect:/login";
        }
    }
}
