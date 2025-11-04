
package com.nayabas.config;

import com.nayabas.entity.User;
import com.nayabas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String adminEmail = "admin@gmail.com";

        if (userRepository.existsByEmail(adminEmail)) return;

        User admin = User.builder()
                .username("admin@gmail.com")
                .email(adminEmail)
                .password(passwordEncoder.encode("admin@gmail.com"))
                .status(User.Status.APPROVED)
                .role(User.Role.ADMIN)
                .fullName("System Admin")
                .isVerified(true)
                .build();

        userRepository.save(admin);
        System.out.println("🚀 Default admin user created: " + adminEmail);
    }
}


//package com.nayabas.config;
//
//import com.nayabas.entity.User;
//import com.nayabas.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Configuration
//@RequiredArgsConstructor
//public class DataInitializer implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    // ✅ Inject values from application.properties
//    @Value("${admin.username}")
//    private String adminUsername;
//
//    @Value("${admin.email}")
//    private String adminEmail;
//
//    @Value("${admin.password}")
//    private String adminPassword;
//
//    @Override
//    public void run(String... args) {
//        if (userRepository.existsByEmail(adminEmail)) {
//            System.out.println("✅ Admin user already exists, skipping creation.");
//            return;
//        }
//
//        User admin = User.builder()
//                .username(adminUsername)
//                .email(adminEmail)
//                .password(passwordEncoder.encode(adminPassword))
//                .role(User.Role.ADMIN)
//                .fullName("System Admin")
//                .isVerified(true)
//                .build();
//
//        userRepository.save(admin);
//        System.out.println("🚀 Default admin user created: " + adminEmail);
//    }
//}
