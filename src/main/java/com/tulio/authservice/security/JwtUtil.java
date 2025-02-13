package com.tulio.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public Key getSigningKey() {
        log.info("Decodificando clave secreta JWT");
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        log.info("Longitud de la clave en bytes: {}", keyBytes.length);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        log.info("Iniciando generación de token para usuario: {}", userDetails.getUsername());
        Map<String, Object> claims = new HashMap<>();
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        log.info("Token generado exitosamente: {}", token);
        return token;
    }

    public String extractUsername(String token) {
        log.info("Extrayendo username del token");
        try {
            String username = extractClaim(token, Claims::getSubject);
            log.info("Username extraído: {}", username);
            return username;
        } catch (Exception e) {
            log.error("Error al extraer username del token", e);
            throw e;
        }
    }

    public Date extractExpiration(String token) {
        log.info("Extrayendo fecha de expiración del token");
        try {
            Date expiration = extractClaim(token, Claims::getExpiration);
            log.info("Fecha de expiración: {}", expiration);
            return expiration;
        } catch (Exception e) {
            log.error("Error al extraer fecha de expiración", e);
            throw e;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        log.info("Extrayendo claims del token: {}", token);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.info("Claims extraídos exitosamente: {}", claims);
            return claims;
        } catch (Exception e) {
            log.error("Error al extraer claims del token", e);
            throw e;
        }
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        log.info("Iniciando validación de token para usuario: {}", userDetails.getUsername());
        try {
            final String username = extractUsername(token);
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            log.info("Token válido: {}", isValid);
            return isValid;
        } catch (Exception e) {
            log.error("Error durante la validación del token", e);
            return false;
        }
    }

    public Boolean isTokenExpired(String token) {
        log.info("Verificando si el token está expirado");
        try {
            boolean isExpired = extractExpiration(token).before(new Date());
            log.info("Token expirado: {}", isExpired);
            return isExpired;
        } catch (Exception e) {
            log.error("Error al verificar expiración del token", e);
            return true;
        }
    }
}