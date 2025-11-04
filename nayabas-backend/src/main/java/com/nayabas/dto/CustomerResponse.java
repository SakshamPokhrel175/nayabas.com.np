package com.nayabas.dto;

import lombok.Data;

@Data
public class CustomerResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private byte[] profileImage; // Base64 image data (BLOB)
}


