package com.awaydays.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private UUID userId;
    private String username;
    private String email;
    private String token;  // JWT token
    private String message;

    // Constructor without token (for error messages)
    public AuthResponse(UUID userId, String username, String email, String message) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.message = message;
    }
}