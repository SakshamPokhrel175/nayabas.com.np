package com.nayabas.controller;

import com.nayabas.dto.UserProfileDto;
import com.nayabas.dto.UserProfileUpdateDto;
import com.nayabas.entity.User;
import com.nayabas.repository.UserRepository;
import com.nayabas.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    // Get current user profile
    @GetMapping("/my-profile")
    public ResponseEntity<UserProfileDto> getProfile(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails) {

        if (userDetails == null) return ResponseEntity.status(401).build();

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(new UserProfileDto(user));
    }

    // Update editable profile fields
    @PutMapping("/update-profile")
    public ResponseEntity<User> updateProfile(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            @RequestPart("user") UserProfileUpdateDto userDto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        User existingUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Ensure DTO id is current logged-in user
        userDto.setId(existingUser.getId());

        User updatedUser = userService.updateProfile(userDto, profileImage);

        return ResponseEntity.ok(updatedUser);
    }
}


