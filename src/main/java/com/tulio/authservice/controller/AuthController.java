package com.tulio.authservice.controller;

import com.tulio.authservice.dto.AuthRequest;
import com.tulio.authservice.dto.AuthResponse;
import com.tulio.authservice.model.User;
import com.tulio.authservice.security.JwtUtil;
import com.tulio.authservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest loginRequest) {
        log.info("Intento de login para usuario: {}", loginRequest.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            log.info("Usuario autenticado exitosamente");

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String jwt = jwtUtil.generateToken(userDetails);
            log.info("Token JWT generado: {}", jwt);

            User user = userService.findByEmail(loginRequest.getEmail());

            return ResponseEntity.ok(new AuthResponse(jwt, user.getId(), user.getName(), user.getEmail()));
        } catch (AuthenticationException e) {
            log.error("Error en la autenticación", e);
            return ResponseEntity.badRequest().body("Error en la autenticación");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        log.info("Recibida solicitud de validación de token");
        log.info("Authorization header: {}", authHeader);

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.error("Header de autorización inválido o ausente");
                return ResponseEntity.badRequest().body("Token inválido");
            }

            String token = authHeader.substring(7);
            log.info("Token extraído del header: {}", token);

            String username = jwtUtil.extractUsername(token);
            log.info("Username extraído del token: {}", username);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("username", username);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al validar el token", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("valid", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }
}