package com.tulio.authservice.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String name;
    private String email;
    private String id;

    public AuthResponse(String token, String name, String email, String id) {
        this.token = token;
        this.name = name;
        this.email = email;
        this.id = id;
    }
}
