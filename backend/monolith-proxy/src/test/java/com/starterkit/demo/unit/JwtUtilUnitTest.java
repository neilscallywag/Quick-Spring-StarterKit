package com.starterkit.demo.unit;

import com.starterkit.demo.exception.AuthenticationException;
import com.starterkit.demo.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "jwt.secret=jwtsecrectjwtsecrectjwtsecrectjwtsecrectjwtsecrectjwtsecrectjwtsecrectjwtsecrectjwtsecrect",
        "jwt.expiration=5000",
        "jwt.clockSkew=5000"
})
class JwtUtilUnitTest {

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        // NIL
    }

    @Test
    void testGenerateAndValidateToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");

        String token = jwtUtil.generateToken(claims, "username");

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token, "username"));
    }

    @Test
    void testGetClaimsFromToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");

        String token = jwtUtil.generateToken(claims, "username");
        Claims extractedClaims = jwtUtil.getClaimsFromToken(token);

        assertNotNull(extractedClaims);
        assertEquals("username", extractedClaims.getSubject());
        assertEquals("USER", extractedClaims.get("role"));
    }

    @Test
    void testGetClaimsFromToken_InvalidToken() {
        String invalidToken = "invalid.token";

        assertThrows(AuthenticationException.class, () -> {
            jwtUtil.getClaimsFromToken(invalidToken);
        });
    }

    @Test
    void testIsTokenExpired() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");

        String token = jwtUtil.generateToken(claims, "username");

        // Token should not be expired immediately after generation
        assertFalse(jwtUtil.isTokenExpired(token));

        // Wait for the token to expire
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(jwtUtil.isTokenExpired(token));
    }

    @Test
    void testValidateToken_ExpiredToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");

        String token = jwtUtil.generateToken(claims, "username");

        // Wait for the token to expire
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertFalse(jwtUtil.validateToken(token, "username"));
    }

    @Test
    void testValidateToken_InvalidUsername() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");

        String token = jwtUtil.generateToken(claims, "username");

        assertFalse(jwtUtil.validateToken(token, "wrongUsername"));
    }

    @Test
    void testValidateToken_Valid() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");

        String token = jwtUtil.generateToken(claims, "username");

        assertTrue(jwtUtil.validateToken(token));
    }

    @Test
    void testValidateToken_Expired() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");

        String token = jwtUtil.generateToken(claims, "username");

        // Wait for the token to expire
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertFalse(jwtUtil.validateToken(token));
    }

    @Test
    void testGetUserNameFromToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");

        String token = jwtUtil.generateToken(claims, "username");
        String username = jwtUtil.getUserNameFromToken(token);

        assertEquals("username", username);
    }
}
