//package com.vegstore.service;
//
//import com.vegstore.entity.User;
//import com.vegstore.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class UserService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Transactional
//    public User registerUser(User user) {
//        if (userRepository.existsByUsername(user.getUsername())) {
//            throw new RuntimeException("Username already exists");
//        }
//
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        log.info("Registering new user: {}", user.getUsername());
//        return userRepository.save(user);
//    }
//
//    public User getUserById(Long id) {
//        return userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//    }
//
//    public User getUserByUsername(String username) {
//        return userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//    }
//
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    public List<User> getSalespersons() {
//        return userRepository.findByRole(User.Role.SALESPERSON);
//    }
//
//    @Transactional
//    public User updateUser(User user) {
//        return userRepository.save(user);
//    }
//
//    @Transactional
//    public void deleteUser(Long id) {
//        userRepository.deleteById(id);
//    }
//}
package com.vegstore.service;

import com.vegstore.entity.User;
import com.vegstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(User user) {
        log.info("Registering user: {}", user.getUsername());

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        String rawPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        log.info("Raw password length: {}", rawPassword.length());
        log.info("Encoded password: {}", encodedPassword.substring(0, 20) + "...");

        user.setPassword(encodedPassword);
        User savedUser = userRepository.save(user);

        log.info("User registered successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getSalespersons() {
        return userRepository.findByRole(User.Role.SALESPERSON);
    }
}

