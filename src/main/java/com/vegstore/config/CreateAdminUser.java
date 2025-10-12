package com.vegstore.config;

import com.vegstore.entity.User;
import com.vegstore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class CreateAdminUser {

    @Bean
    public CommandLineRunner createUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            // Create Admin
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setFullName("Admin User");
                admin.setRole(User.Role.ADMIN);
                admin.setIsWholesale(false);
                userRepository.save(admin);

                System.out.println("✅ Admin created: admin / admin123");
            }

            // Create Salesperson
            if (userRepository.findByUsername("sales1").isEmpty()) {
                User sales = new User();
                sales.setUsername("sales1");
                sales.setPassword(passwordEncoder.encode("sales123"));
                sales.setFullName("Sales Person");
                sales.setRole(User.Role.SALESPERSON);
                sales.setIsWholesale(false);
                userRepository.save(sales);

                System.out.println("✅ Salesperson created: sales1 / sales123");
            }

            System.out.println("========================================");
            System.out.println("LOGIN CREDENTIALS:");
            System.out.println("Admin: admin / admin123");
            System.out.println("Sales: sales1 / sales123");
            System.out.println("========================================");
        };
    }
}
