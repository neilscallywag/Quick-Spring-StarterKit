/* (C)2024 */
package com.starterkit.demo.util;

import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.starterkit.demo.exception.AuthenticationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    @Setter
    private String secret;

    @Value("${jwt.expiration}")
    @Setter
    private Long expiration;

    @Value("${jwt.clockSkew}")
    @Setter
    private Long clockSkew;

    @PostConstruct
    public void init() {
        log.info("JWT Secret: {}", secret);
        log.info("JWT Expiration: {}", expiration);
        log.info("JWT Clock Skew: {}", clockSkew);
    }
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(this.secret.getBytes());
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
}
