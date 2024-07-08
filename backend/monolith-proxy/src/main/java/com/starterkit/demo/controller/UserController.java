/* (C)2024 */
package com.starterkit.demo.controller;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starterkit.demo.dto.*;
import com.starterkit.demo.exception.*;
import com.starterkit.demo.model.User;
import com.starterkit.demo.service.UserService;
import com.starterkit.demo.util.JwtUtil;
import com.starterkit.demo.util.PaginationUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nameFilter,
            @RequestParam(required = false) String emailFilter,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortOrder) {

        Page<User> userPage =
                userService.getAllUsers(page, size, nameFilter, emailFilter, sortField, sortOrder);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(userPage, "/api/users");
        List<UserResponseDTO> userResponses =
                userPage.getContent().stream()
                        .map(UserResponseDTO::convertToUserResponseDTO)
                        .collect(Collectors.toList());
        return ResponseEntity.ok().headers(headers).body(userResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        UserResponseDTO returnUser = UserResponseDTO.convertToUserResponseDTO(user);
        return ResponseEntity.ok(returnUser);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody NewUserRequestDTO user) {
        validateUser(user);
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable UUID id, @Valid @RequestBody User userDetails) {
        validateUser(userDetails);
        User updatedUser = userService.updateUser(id, userDetails);
        if (updatedUser == null) {
            throw new ResourceNotFoundException("User not found with id " + id);
        }
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @Valid @RequestBody LocalLoginRequestDTO data, HttpServletResponse response) {
        String username = data.getUsername().toLowerCase();
        String password = data.getPassword();
        try {
            return ResponseEntity.ok(userService.login(username, password, response));
        } catch (AuthenticationException ex) {
            throw new InvalidRequestException("Invalid username or password");
        }
    }

    @PostMapping("/me")
    public ResponseEntity<MeResponseDTO> getMe(
            @CookieValue(name = "JWT_TOKEN", required = false) String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidRequestException("Token is missing");
        }
        Claims claims = jwtUtil.getClaimsFromToken(token);

        MeResponseDTO userInfoResponse = new MeResponseDTO();
        userInfoResponse.setUsername(claims.getSubject());

        // Safe conversion of the roles claim
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> roles =
                objectMapper.convertValue(
                        claims.get("roles"), new TypeReference<List<String>>() {});
        userInfoResponse.setRoles(roles);

        userInfoResponse.setIssuedAt(claims.getIssuedAt());
        userInfoResponse.setExpiresAt(claims.getExpiration());

        return ResponseEntity.ok(userInfoResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpServletResponse response,
            @CookieValue(name = "JWT_TOKEN", required = false) String token) {
        userService.logout(response, token);
        return ResponseEntity.ok("Logged out successfully");
    }

    private void validateUser(NewUserRequestDTO user) {
        if (Objects.isNull(user.getUsername())
                || user.getUsername().isBlank()
                || Objects.isNull(user.getEmail())
                || user.getEmail().isBlank()
                || Objects.isNull(user.getPassword())
                || user.getPassword().isBlank()) {
            throw new InvalidRequestException("User details cannot be null or incomplete");
        }
    }

    private void validateUser(User user) {
        if (Objects.isNull(user.getUsername())
                || user.getUsername().isBlank()
                || Objects.isNull(user.getEmail())
                || user.getEmail().isBlank()
                || Objects.isNull(user.getPassword())
                || user.getPassword().isBlank()) {
            throw new InvalidRequestException("User details cannot be null or incomplete");
        }
    }
}
