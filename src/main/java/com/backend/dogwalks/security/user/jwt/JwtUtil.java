package com.backend.dogwalks.security.jwt;

import com.backend.dogwalks.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    //inyecta valores desde application.properties
    @Value("${app.jwt.secret}")
    private String jwtSecretKey;

    @Value("${app.jwt.expiration}")
    private Long jwtExpiration;

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String buildToken(CustomUserDetails userDetail, long jwtExpiration) {
        return Jwts
                .builder()
                .subject(userDetail.getUsername())
                .claim("id", userDetail.getId())
                .claim("role", userDetail.getAuthorities().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey())
                .compact();
    }

    public String generateToken(CustomUserDetails userDetail) {
        return buildToken(userDetail, jwtExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractId(String token) {
        return extractAllClaims(token).get("id", Long.class);
    }

    public boolean isValidToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch(Exception exception) {
            return false;
        }
    }
}
