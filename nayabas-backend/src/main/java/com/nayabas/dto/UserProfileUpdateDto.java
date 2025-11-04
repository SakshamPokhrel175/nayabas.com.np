package com.nayabas.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileUpdateDto {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String addressLine1;
    private String password;
}
