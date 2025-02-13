package com.tulio.authservice.controller;

import com.tulio.authservice.dto.JwtValidationResponse;
import com.tulio.authservice.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/auth")
public class TokenController {
    private final JwtUtil jwtUtil;

    public TokenController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/validate")
    public ResponseEntity<JwtValidationResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remover "Bearer "
            String username = jwtUtil.extractUsername(token);
            boolean isValid = !jwtUtil.isTokenExpired(token);
            return ResponseEntity.ok(new JwtValidationResponse(isValid, username));
        } catch (Exception e) {
            return ResponseEntity.ok(new JwtValidationResponse(false, null));
        }
    }
}
