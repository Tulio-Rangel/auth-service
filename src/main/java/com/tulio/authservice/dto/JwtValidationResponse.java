package com.tulio.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtValidationResponse {
    private boolean valid;
    private String username;
}
