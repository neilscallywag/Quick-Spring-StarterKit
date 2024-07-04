package com.starterkit.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.clockSkew}")
    private Long clockSkew;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims) 
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(),  Jwts.SIG.HS512)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .clockSkewSeconds(clockSkew / 1000) // converting milliseconds to seconds
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public boolean isTokenExpired(String token) {
        final Date tokenExpiration = getClaimsFromToken(token).getExpiration();
        return tokenExpiration.before(new Date());
    }

    public boolean validateToken(String token, String username) {
        final String usernameFromToken = getClaimsFromToken(token).getSubject();
        return (usernameFromToken.equals(username) && !isTokenExpired(token));
    }
    public Long getExpiration() {
        return expiration;
    }
}
