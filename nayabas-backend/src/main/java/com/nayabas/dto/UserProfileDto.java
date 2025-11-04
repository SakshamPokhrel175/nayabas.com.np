package com.nayabas.dto;

import java.util.Base64;

import com.nayabas.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileDto {
    private String username;
    private String fullName;
    private String email;
    private String role;
    private String phoneNumber;
    private String addressLine1;  // <-- add this

    private String profileImageBase64;
    private String idProofBase64;
    private String houseOwnershipProofBase64;

    public UserProfileDto(User user) {
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.phoneNumber = user.getPhoneNumber();
        this.addressLine1 = user.getAddressLine1();  // <-- populate here

        this.profileImageBase64 = user.getProfileImage() != null
                ? Base64.getEncoder().encodeToString(user.getProfileImage())
                : null;

        this.idProofBase64 = user.getIdProof() != null
                ? Base64.getEncoder().encodeToString(user.getIdProof())
                : null;

        this.houseOwnershipProofBase64 = user.getHouseOwnershipProof() != null
                ? Base64.getEncoder().encodeToString(user.getHouseOwnershipProof())
                : null;
    }
}

