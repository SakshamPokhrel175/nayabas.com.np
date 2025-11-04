package com.nayabas.dto;

import lombok.Data;

@Data
public class SellerRegisterRequest {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String addressLine1;
    private byte[] idProof;
    private byte[] houseOwnershipProof;

}