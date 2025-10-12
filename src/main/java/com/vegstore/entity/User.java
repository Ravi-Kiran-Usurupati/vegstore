////package com.vegstore.entity;
////
////import jakarta.persistence.*;
////import jakarta.validation.constraints.NotBlank;
////import lombok.*;
////
////@Entity
////@Table(name = "users")
////@Data
////@NoArgsConstructor
////@AllArgsConstructor
////@Builder
////public class User {
////
////    @Id
////    @GeneratedValue(strategy = GenerationType.IDENTITY)
////    private Long id;
////
////    @NotBlank(message = "Username is required")
////    @Column(unique = true, nullable = false)
////    private String username;
////
////    @NotBlank(message = "Password is required")
////    @Column(nullable = false)
////    private String password;
////
////    @NotBlank(message = "Full name is required")
////    private String fullName;
////
////    @Enumerated(EnumType.STRING)
////    @Column(nullable = false)
////    private Role role;
////
////    @Column(nullable = false)
////    private Boolean isWholesale = false;
////
////    public enum Role {
////        ADMIN, SALESPERSON, CUSTOMER
////    }
////}
//package com.vegstore.entity;
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotBlank;
//import lombok.*;
//
//@Entity
//@Table(name = "users")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class User {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @NotBlank(message = "Username is required")
//    @Column(unique = true, nullable = false, length = 50)
//    private String username;
//
//    @NotBlank(message = "Password is required")
//    @Column(nullable = false, length = 255)
//    private String password;
//
//    @NotBlank(message = "Full name is required")
//    @Column(nullable = false, length = 100)
//    private String fullName;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false, length = 20)
//    private Role role;
//
//    @Column(nullable = false)
//    @Builder.Default
//    private Boolean isWholesale = false;
//
//    public enum Role {
//        ADMIN, SALESPERSON, CUSTOMER
//    }
//}
package com.vegstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Full name is required")
    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private Boolean isWholesale = false;

    public enum Role {
        ADMIN, SALESPERSON, CUSTOMER
    }
}
