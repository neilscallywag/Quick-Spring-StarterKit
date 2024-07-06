package com.starterkit.demo.unit;

import com.starterkit.demo.util.JwtUtil;
import io.jsonwebtoken.Claims;
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
        "jwt.expiration=50000",
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
}
