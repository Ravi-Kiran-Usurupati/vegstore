//package com.vegstore.controller;
//
//import com.vegstore.entity.User;
//import com.vegstore.service.CustomUserDetailsService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ModelAttribute;
//
//@ControllerAdvice
//@RequiredArgsConstructor
//public class GlobalControllerAdvice {
//
//    private final CustomUserDetailsService userDetailsService;
//
//    @ModelAttribute("currentUser")
//    public User getCurrentUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication != null && authentication.isAuthenticated()
//                && !authentication.getPrincipal().equals("anonymousUser")) {
//            String username = authentication.getName();
//            try {
//                return userDetailsService.getUserByUsername(username);
//            } catch (Exception e) {
//                return null;
//            }
//        }
//        return null;
//    }
//}
package com.vegstore.controller;

import com.vegstore.entity.User;
import com.vegstore.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalControllerAdvice {

    private final CustomUserDetailsService userDetailsService;

    @ModelAttribute("currentUser")
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")
                && !authentication.getName().equals("anonymousUser")) {

            String username = authentication.getName();
            try {
                User user = userDetailsService.getUserByUsername(username);
                log.debug("Current user loaded: {} ({})", user.getUsername(), user.getRole());
                return user;
            } catch (Exception e) {
                log.debug("No authenticated user found");
                return null;
            }
        }
        return null;
    }
}
