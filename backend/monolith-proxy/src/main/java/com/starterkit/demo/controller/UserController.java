package com.starterkit.demo.controller;

import com.starterkit.demo.dto.LocalLoginRequestDTO;
import com.starterkit.demo.dto.MeResponseDTO;
import com.starterkit.demo.dto.NewUserRequestDTO;
import com.starterkit.demo.dto.UserResponseDTO;
import com.starterkit.demo.exception.InvalidRequestException;
import com.starterkit.demo.exception.ResourceNotFoundException;
import com.starterkit.demo.exception.AuthenticationException;
import com.starterkit.demo.model.User;
import com.starterkit.demo.service.UserService;
import com.starterkit.demo.util.JwtUtil;
import com.starterkit.demo.util.PaginationUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.togglz.core.manager.FeatureManager;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FeatureManager featureManager;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String nameFilter,
        @RequestParam(required = false) String emailFilter,
        @RequestParam(defaultValue = "id") String sortField,
        @RequestParam(defaultValue = "asc") String sortOrder) {

    Page<User> userPage = userService.getAllUsers(page, size, nameFilter, emailFilter, sortField, sortOrder);
    HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(userPage, "/api/users");
    List<UserResponseDTO> userResponses = userPage.getContent().stream()
            .map(UserResponseDTO::convertToUserResponseDTO)
            .collect(Collectors.toList());
    return ResponseEntity.ok().headers(headers).body(userResponses);
}
    

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        if (id == null) {
            throw new InvalidRequestException("User ID cannot be null");
        }
        User user = userService.getUserById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody NewUserRequestDTO user) {
        validateUser(user);
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @Valid @RequestBody User userDetails) {
        if (id == null || userDetails == null) {
            throw new InvalidRequestException("User ID and details cannot be null");
        }
        validateUser(userDetails);
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        if (id == null) {
            throw new InvalidRequestException("User ID cannot be null");
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LocalLoginRequestDTO data, HttpServletResponse response) {
        if (data == null || Objects.isNull(data.getUsername()) || Objects.isNull(data.getPassword())
                || data.getUsername().isEmpty() || data.getPassword().isEmpty()) {
            throw new InvalidRequestException("Username and password cannot be null");
        }
        try {
            String username = data.getUsername().toLowerCase();
            String password = data.getPassword();
            return ResponseEntity.ok(userService.login(username, password, response));
        } catch (Exception e) {
            throw new AuthenticationException("Invalid username or password");
        }
    }

    @PostMapping("/me")
    public ResponseEntity<MeResponseDTO> getMe(@CookieValue(name = "JWT_TOKEN", required = false) String token) {
        if (token == null) {
            throw new InvalidRequestException("JWT token is required");
        }
        try {
            if (jwtUtil.isTokenExpired(token)) {
                throw new InvalidRequestException("JWT token has expired");
            }

            Claims claims = jwtUtil.getClaimsFromToken(token);

            MeResponseDTO userInfoResponse = new MeResponseDTO();
            userInfoResponse.setUsername(claims.getSubject());
            userInfoResponse.setRoles((List<String>) claims.get("roles"));
            userInfoResponse.setIssuedAt(claims.getIssuedAt());
            userInfoResponse.setExpiresAt(claims.getExpiration());

            return ResponseEntity.ok(userInfoResponse);
        } catch (Exception e) {
            throw new InvalidRequestException("Failed to retrieve user information from token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response,
            @CookieValue(name = "JWT_TOKEN", required = false) String token) {
        if (token == null) {
            throw new InvalidRequestException("JWT token is required for logout");
        }
        try {
            userService.logout(response, token);
            return ResponseEntity.ok("Logged out successfully");
        } catch (Exception e) {
            throw new InvalidRequestException("Failed to logout");
        }
    }

    private void validateUser(NewUserRequestDTO user) {
        if (user == null || Objects.isNull(user.getUsername()) || user.getUsername().isBlank() ||
                Objects.isNull(user.getEmail()) || user.getEmail().isBlank() ||
                Objects.isNull(user.getPassword()) || user.getPassword().isBlank()) {
            throw new InvalidRequestException("User details cannot be null or incomplete");
        }
    }
    private void validateUser(User user) {
        if (user == null || Objects.isNull(user.getUsername()) || user.getUsername().isBlank() ||
                Objects.isNull(user.getEmail()) || user.getEmail().isBlank() ||
                Objects.isNull(user.getPassword()) || user.getPassword().isBlank()) {
            throw new InvalidRequestException("User details cannot be null or incomplete");
        }
    }
}