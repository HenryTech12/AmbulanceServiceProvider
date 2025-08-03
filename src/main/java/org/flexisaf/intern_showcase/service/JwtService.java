package org.flexisaf.intern_showcase.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.flexisaf.intern_showcase.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    private String secretKey;

    private SecretKey createKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey key = keyGenerator.generateKey();
            secretKey = Base64.getEncoder().encodeToString(key.getEncoded());
            return key;
        }
        catch(NoSuchAlgorithmException e) {
            log.error("an error occurred!!: {}",e.getMessage());
        }
        return null;
    }

    public String generateJwtToken(UserDTO userDTO) {
        Map<String,String> claims = new HashMap<>();

        return Jwts.builder()
                .subject(userDTO.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() * 1000 + 20))
                .signWith(createKey())
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public <T>T extractClaims(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    public SecretKey getKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String email = extractEmail(token);
        return (!isTokenExpired(token) && Objects.equals(email,userDetails.getUsername()));
    }

    public boolean isTokenExpired(String token) {
        return new Date(System.currentTimeMillis())
                .after(extractClaims(token,Claims::getExpiration));
    }

    public String extractEmail(String token) {
        return extractClaims(token,Claims::getSubject);
    }
}
