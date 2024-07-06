package com.starterkit.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.starterkit.demo.exception.AuthenticationException;

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
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        try {

            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .clockSkewSeconds(clockSkew / 1000)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (JwtException e) {
            // Any exception we will return invalid token so that the frontend can catch and log
            // the user out
            throw new AuthenticationException("Invalid Token");
        }  
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

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public String getUserNameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    // Setters for testing purposes
    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public void setClockSkew(Long clockSkew) {
        this.clockSkew = clockSkew;
    }
}
