Veg Store Spring Boot project skeleton
--------------------------------------

1. Edit src/main/resources/application.properties with your MySQL credentials.
2. Create database 'vegstore' in MySQL (CREATE DATABASE vegstore;).
3. Build & run:
   mvn clean package
   mvn spring-boot:run

This project includes:
- Java 21, Spring Boot, Thymeleaf, Bootstrap (CDN)
- Entities with getters/setters
- Repositories, services, controllers
- Basic role-based security (ADMIN, SALESPERSON, CUSTOMER)
- Templates for basic flows

Notes:
- Password for initial users must be created via registration or by inserting a BCrypt hashed password into DB.
- Payment integration is left as a stub (PaymentRecord entity + paymentStatus).
- For production, use Flyway/Liquibase and proper configuration.
