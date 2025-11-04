package com.nayabas.service;

import com.nayabas.dto.CustomerRegisterRequest;
import com.nayabas.dto.SellerRegisterRequest;
import com.nayabas.dto.UserProfileUpdateDto;
import com.nayabas.entity.User;
import com.nayabas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ======== FIND ALL SELLERS ========
    public List<User> findAllSellers() {
        return userRepository.findByRole(User.Role.SELLER);
    }

    // ======== FIND ALL BUYERS ========
    public List<User> findAllBuyers() {
        return userRepository.findByRole(User.Role.CUSTOMER);
    }

    // ======== VERIFY/APPROVE SELLER ========
    public User verifySeller(Long id, boolean approve, String notes) {
        User seller = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Seller not found with id: " + id));

        if (approve) {
            seller.setStatus(User.Status.APPROVED);
        } else {
            seller.setStatus(User.Status.REJECTED);
        }
        seller.setVerificationNotes(notes);

        return userRepository.save(seller);
    }

    // ======== REGISTER CUSTOMER ========
    public User registerCustomer(CustomerRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        User customer = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .addressLine1(request.getAddressLine1())
                .role(User.Role.CUSTOMER)
                .status(User.Status.APPROVED) // ✅ auto-approved
                .build();

        return userRepository.save(customer);
    }

    // ======== REGISTER SELLER ========
    public User registerSeller(SellerRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        User seller = User.builder()
        	    .username(request.getUsername())
        	    .password(passwordEncoder.encode(request.getPassword()))
        	    .email(request.getEmail())
        	    .fullName(request.getFullName())
        	    .phoneNumber(request.getPhoneNumber())
        	    .addressLine1(request.getAddressLine1())
        	    .idProof(request.getIdProof()) // 👈 use byte[] instead of URL
        	    .houseOwnershipProof(request.getHouseOwnershipProof()) // 👈 use byte[]
        	    .role(User.Role.SELLER)
        	    .status(User.Status.PENDING)
        	    .build();


        return userRepository.save(seller);
    }

    // ======== FIND BY USERNAME ========
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    // ======== FIND PENDING SELLERS ========
    public List<User> findPendingSellers() {
        return userRepository.findByRoleAndStatus(User.Role.SELLER, User.Status.PENDING);
    }

    // ======== COUNT TOTAL SELLERS ========
    public long countSellers() {
        return userRepository.countByRole(User.Role.SELLER);
    }

    // ======== COUNT TOTAL BUYERS ========
    public long countBuyers() {
        return userRepository.countByRole(User.Role.CUSTOMER);
    }

    // ======== COUNT PENDING SELLERS ========
    public long countPendingSellers() {
        return userRepository.countByRoleAndStatus(User.Role.SELLER, User.Status.PENDING);
    }

    // ======== BASIC CRUD HELPERS ========
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

 // ======== UPDATE PROFILE ========
    public User updateProfile(UserProfileUpdateDto userDto, MultipartFile profileImage) {
        User user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Editable fields only
        user.setFullName(userDto.getFullName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setAddressLine1(userDto.getAddressLine1());

        // Change password only if provided
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        // Update profile image if uploaded
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                user.setProfileImage(profileImage.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload profile image", e);
            }
        }

        // Save and return updated user
        return userRepository.save(user);
    }


}
