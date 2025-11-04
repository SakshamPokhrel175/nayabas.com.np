package com.nayabas.dto;

import lombok.Data;

@Data
public class CustomerRegisterRequest {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String addressLine1;
}