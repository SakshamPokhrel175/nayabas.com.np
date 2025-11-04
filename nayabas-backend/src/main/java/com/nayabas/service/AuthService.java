package com.nayabas.service;

import com.nayabas.dto.*;
import com.nayabas.entity.User;
import com.nayabas.repository.UserRepository;
import com.nayabas.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

 // Register Customer (Renter)
    public JwtResponse registerCustomer(CustomerRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(User.Role.CUSTOMER)
                .status(User.Status.APPROVED) // ✅ Customers are auto-approved
                .fullName(request.getFullName())
                .addressLine1(request.getAddressLine1())
                .isVerified(true)
                .build();

        userRepository.save(user);

        String token = jwtUtils.generateJwtToken(
                user.getUsername(),
                user.getRole().name(),
                user.getIsVerified()
        );

        return JwtResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .build();
        }


 // Register Seller (needs admin verification)
    public JwtResponse registerSeller(SellerRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(User.Role.SELLER)
                .status(User.Status.PENDING) // ✅ Seller starts as PENDING
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .addressLine1(request.getAddressLine1())
                .idProof(request.getIdProof())
                .houseOwnershipProof(request.getHouseOwnershipProof())
                .isVerified(false)
                .build();

        userRepository.save(user);

        String token = jwtUtils.generateJwtToken(
                user.getUsername(),
                user.getRole().name(),
                user.getIsVerified()
        );

        return JwtResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .build();
        }


    // Login (for any user)
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtils.generateJwtToken(user.getUsername(), user.getRole().name(), Boolean.TRUE.equals(user.getIsVerified()));

        return JwtResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .status(user.getStatus().name()) // ✅ Now status is included!
                .build();
        }
}