package com.nayabas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // ✅ This generates the builder() method
public class JwtResponse {
    private String token;
    private String username;
    private String role;
    private String status; // status field included
}
