package com.anu.job_tracker.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

    @Service
    public class JwtService {

        @Value("${jwt.secret}")
        private String jwtSecret;

        @Value("${jwt.expiration}")
        private long jwtExpiration;

        public String generateToken(String email) {
            return Jwts.builder()
                    .subject(email)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(getSigningKey())
                    .compact();
        }

        public String extractEmail(String token) {
            return extractAllClaims(token).getSubject();
        }

        public boolean isTokenValid(String token, String email) {
            return email.equals(extractEmail(token))
                    && !isTokenExpired(token);
        }

        private boolean isTokenExpired(String token) {
            return extractAllClaims(token)
                    .getExpiration()
                    .before(new Date());
        }

        private Claims extractAllClaims(String token) {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }

        private SecretKey getSigningKey() {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }