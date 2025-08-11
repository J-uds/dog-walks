package com.backend.dogwalks.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    //inyecta valores desde application.properties
    @Value("${app.jwt.secret}")
    private String jwtSecretKey;

    @Value("${app.jwt.expiration}")
    private Long jwtExpiration;

    public String generateToken(CustomUserDetails userDetail) {
        return buildToken(userDetail, jwtExpiration);
    }

    private String buildToken(CustomUserDetails userDetail, long jwtExpiration) {
        return Jwts
                .builder()
                .subject(userDetail.getUsername())
                .claim("role", userDetail.getAuthorities().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isValidToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch(Exception exception) {
            return false;
        }
    }

    private SecretKey getSignKey() {
        byte[] bytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
