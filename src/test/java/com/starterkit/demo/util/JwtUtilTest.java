package com.starterkit.demo.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
 class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @Mock
    private SecretKey secretKey;

    private final String secret = "mySecretKeymySecretKeymySecretKeymySecretKeymySecretKeymySecretKeymySecretKeymySecretKey"; // 64 bytes for HS512
    private final Long expiration = 3600000L; // 1 hour
    private final Long clockSkew = 5000L; // 5 seconds

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);
        ReflectionTestUtils.setField(jwtUtil, "clockSkew", clockSkew);
        secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }
    @Test
    void testGenerateToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "admin");
        String subject = "user1";

        String token = jwtUtil.generateToken(claims, subject);

        assertNotNull(token);

        Claims parsedClaims = Jwts.parser()
                .verifyWith(secretKey)
                .clockSkewSeconds(clockSkew/ 1000)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(subject, parsedClaims.getSubject());
        assertEquals("admin", parsedClaims.get("role"));
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void testGetClaimsFromToken() {
        String token = jwtUtil.generateToken(new HashMap<>(), "user1");

        Claims claims = jwtUtil.getClaimsFromToken(token);

        assertEquals("user1", claims.getSubject());
    }

    @Test
    void testIsTokenExpired() {
        String token = jwtUtil.generateToken(new HashMap<>(), "user1");

        assertFalse(jwtUtil.isTokenExpired(token));

        // Simulate token expiration by waiting for the token to expire
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1L); // setting expiration in the past

        token = jwtUtil.generateToken(new HashMap<>(), "user1");
        assertTrue(jwtUtil.isTokenExpired(token));
    }

    @Test
    void testValidateToken() {
        String username = "user1";
        String token = jwtUtil.generateToken(new HashMap<>(), username);

        assertTrue(jwtUtil.validateToken(token, username));
        assertFalse(jwtUtil.validateToken(token, "user2"));

        // Simulate token expiration
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1L); // setting expiration in the past

        token = jwtUtil.generateToken(new HashMap<>(), username);
        assertFalse(jwtUtil.validateToken(token, username));
    }
}
