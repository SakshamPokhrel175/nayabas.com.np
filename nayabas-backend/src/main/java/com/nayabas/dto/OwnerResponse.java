package com.nayabas.dto;

import lombok.Data;

@Data
public class OwnerResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String role;
}