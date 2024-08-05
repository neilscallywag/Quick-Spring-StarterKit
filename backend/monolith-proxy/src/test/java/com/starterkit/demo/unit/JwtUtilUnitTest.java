package com.starterkit.demo.unit;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import com.starterkit.demo.exception.AuthenticationException;
import com.starterkit.demo.util.JwtUtil;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilUnitTest {

	private JwtUtil jwtUtil;

	private final String secret = "jwtsecrectjwtsecrectjwtsecrectjwtsecrectjwtsecrectjwtsecrectjwtsecrectjwtsecrectjwtsecrect";
	private final Long expiration = 5000L;
	private final Long clockSkew = 5000L;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		jwtUtil = new JwtUtil();
		jwtUtil.setSecret(secret);
		jwtUtil.setExpiration(expiration);
		jwtUtil.setClockSkew(clockSkew);
	}

	@Test
	void testGenerateToken() {
		Map<String, Object> claims = new HashMap<>();
		String token = jwtUtil.generateToken(claims, "user");
		assertNotNull(token);
	}

	@Test
	void testGetClaimsFromToken() {
		Map<String, Object> claims = new HashMap<>();
		String token = jwtUtil.generateToken(claims, "user");
		Claims extractedClaims = jwtUtil.getClaimsFromToken(token);
		assertNotNull(extractedClaims);
		assertEquals("user", extractedClaims.getSubject());
	}

	@Test
	void testGetClaimsFromToken_InvalidToken() {
		assertThrows(AuthenticationException.class, () -> {
			jwtUtil.getClaimsFromToken("invalidToken");
		});
	}

	@Test
	void testGetUserNameFromToken() {
		Map<String, Object> claims = new HashMap<>();
		String token = jwtUtil.generateToken(claims, "user");
		String username = jwtUtil.getUserNameFromToken(token);
		assertEquals("user", username);
	}

	@Test
	void testIsTokenExpired() {
		Map<String, Object> claims = new HashMap<>();
		String token = jwtUtil.generateToken(claims, "user");
		assertFalse(jwtUtil.isTokenExpired(token));
	}

	@Test
	void testValidateToken_Expired() {
		SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
		String expiredToken = Jwts.builder()
				.subject("user")
				.issuedAt(new Date(System.currentTimeMillis() - expiration))
				.expiration(new Date(System.currentTimeMillis() - 1000))
				.signWith(key)
				.compact();
		assertFalse(jwtUtil.validateToken(expiredToken, "user"));
	}

	@Test
	void testValidateToken_InvalidUsername() {
		Map<String, Object> claims = new HashMap<>();
		String token = jwtUtil.generateToken(claims, "user");
		assertFalse(jwtUtil.validateToken(token, "invalidUser"));
	}

	@Test
	void testValidateToken_Valid() {
		Map<String, Object> claims = new HashMap<>();
		String token = jwtUtil.generateToken(claims, "user");
		assertTrue(jwtUtil.validateToken(token, "user"));
	}
}
