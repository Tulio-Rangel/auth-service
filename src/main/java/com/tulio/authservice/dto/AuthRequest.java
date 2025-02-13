package com.tulio.authservice.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
