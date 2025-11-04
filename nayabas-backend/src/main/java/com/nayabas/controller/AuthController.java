package com.nayabas.controller;

import com.nayabas.dto.*;
import com.nayabas.entity.User;
import com.nayabas.repository.UserRepository;
import com.nayabas.security.JwtUtils;
import com.nayabas.service.AuthService;
import com.nayabas.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService; // ✅ Added missing dependency
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ================== CUSTOMER REGISTRATION ==================
    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegisterRequest request) {
        User user = userService.registerCustomer(request);
        return ResponseEntity.ok(user);
    }

    // ================== SELLER REGISTRATION (JSON ONLY) ==================
    @PostMapping("/register/seller")
    public ResponseEntity<?> registerSeller(@RequestBody SellerRegisterRequest request) {
        User user = userService.registerSeller(request);
        return ResponseEntity.ok(user);
    }

    // ================== SELLER REGISTRATION (WITH FILE UPLOAD) ==================
    @PostMapping(value = "/register/seller/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<?> registerSellerWithFiles(
            @RequestPart("username") String username,
            @RequestPart("password") String password,
            @RequestPart("email") String email,
            @RequestPart("fullName") String fullName,
            @RequestPart("addressLine1") String addressLine1,
            @RequestPart("phoneNumber") String phoneNumber,
            @RequestPart(value = "idProof", required = false) MultipartFile idProof,
            @RequestPart(value = "houseOwnershipProof", required = false) MultipartFile houseOwnershipProof
    ) throws IOException {

        SellerRegisterRequest request = new SellerRegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setEmail(email);
        request.setFullName(fullName);
        request.setAddressLine1(addressLine1);
        request.setPhoneNumber(phoneNumber);

        if (idProof != null) request.setIdProof(idProof.getBytes());
        if (houseOwnershipProof != null) request.setHouseOwnershipProof(houseOwnershipProof.getBytes());

        JwtResponse response = authService.registerSeller(request);
        return ResponseEntity.ok(response);
    }


    // ================== LOGIN ==================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getStatus() != User.Status.APPROVED) {
                return ResponseEntity
                        .status(403)
                        .body("Account is not approved by admin yet");
            }

            String token = jwtUtils.generateJwtToken(
                    user.getUsername(),
                    user.getRole().name(),
                    true
            );

            JwtResponse response = new JwtResponse(
                    token,
                    user.getUsername(),
                    user.getRole().name(),
                    user.getStatus().name()
                    
            );

            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }
    
    @PostMapping("/upload/file")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileUrl = "https://cdn.nayabas.com/uploads/" + file.getOriginalFilename(); // or save locally
        // save file to storage (S3, disk, DB)
        return ResponseEntity.ok(Map.of("fileUrl", fileUrl));
    }
}





//package com.nayabas.controller;
//
//import com.nayabas.dto.*;
//import com.nayabas.entity.User;
//import com.nayabas.repository.UserRepository;
//import com.nayabas.security.JwtUtils;
//import com.nayabas.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final UserService userService;
//    private final AuthenticationManager authenticationManager;
//    private final JwtUtils jwtUtils;
//    private final UserRepository userRepository;
//
//    // ================== CUSTOMER REGISTRATION ==================
//    @PostMapping("/register/customer")
//    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegisterRequest request) {
//        User user = userService.registerCustomer(request);
//        return ResponseEntity.ok(user);
//    }
//
//    // ================== SELLER REGISTRATION ==================
//    @PostMapping("/register/seller")
//    public ResponseEntity<?> registerSeller(@RequestBody SellerRegisterRequest request) {
//        User user = userService.registerSeller(request);
//        return ResponseEntity.ok(user);
//    }
//
//    // ================== LOGIN ==================
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            request.getUsername(),
//                            request.getPassword()
//                    )
//            );
//
//            User user = userRepository.findByUsername(request.getUsername())
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            if (user.getStatus() != User.Status.APPROVED) {
//                return ResponseEntity
//                        .status(403)
//                        .body("Account is not approved by admin yet");
//            }
//
//            String token = jwtUtils.generateJwtToken(
//                    user.getUsername(),
//                    user.getRole().name(),
//                    true
//            );
//
//            JwtResponse response = new JwtResponse(
//                    token,
//                    user.getUsername(),
//                    user.getRole().name()
//            );
//
//            return ResponseEntity.ok(response);
//
//        } catch (AuthenticationException e) {
//            return ResponseEntity.status(401).body("Invalid username or password");
//        }
//    }
//    
//    
//}
