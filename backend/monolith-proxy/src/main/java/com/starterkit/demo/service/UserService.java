package com.starterkit.demo.service;

import com.starterkit.demo.dto.RoleDTO;
import com.starterkit.demo.dto.UserResponseDTO;
import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.model.User;
import com.starterkit.demo.repository.UserRepository;
import com.starterkit.demo.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Page<User> getAllUsers(int page, int size, String nameFilter, String emailFilter) {
        Pageable pageable = PageRequest.of(page, size);
        if (nameFilter != null && emailFilter != null) {
            return userRepository.findByNameContainingAndEmailContaining(nameFilter, emailFilter, pageable);
        } else if (nameFilter != null) {
            return userRepository.findByNameContaining(nameFilter, pageable);
        } else if (emailFilter != null) {
            return userRepository.findByEmailContaining(emailFilter, pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserResponseDTO createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User generatedUser = userRepository.save(user);

        UserResponseDTO response = new UserResponseDTO();
        response.setId(generatedUser.getId());
        response.setUsername(generatedUser.getUsername());
        response.setName(generatedUser.getName());
        response.setEmail(generatedUser.getEmail());
        response.setPhoneNumber(generatedUser.getPhoneNumber());
        response.setDateOfBirth(generatedUser.getDateOfBirth());

        Set<RoleDTO> roleDTOs = generatedUser.getRoles().stream().map(role -> {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setId(role.getId());
            roleDTO.setName(EnumRole.valueOf(role.getName().name()));
            return roleDTO;
        }).collect(Collectors.toSet());
        response.setRoles(roleDTOs);

        response.setProvider(generatedUser.getProvider());
        response.setImageUrl(generatedUser.getImageUrl());
        response.setEmailVerified(generatedUser.getEmailVerified());
        response.setAuthProvider(generatedUser.getAuthProvider());

        return response;
    }

    public User updateUser(UUID id, User userDetails) {
        User user = getUserById(id);
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        user.setName(userDetails.getName());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setDateOfBirth(userDetails.getDateOfBirth());
        user.setRoles(userDetails.getRoles());
        user.setProvider(userDetails.getProvider());
        user.setProviderId(userDetails.getProviderId());
        user.setImageUrl(userDetails.getImageUrl());
        user.setEmailVerified(userDetails.getEmailVerified());
        user.setAuthProvider(userDetails.getAuthProvider());
        return userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    public String login(String username, String password, HttpServletResponse response) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        if (passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtUtil.generateToken(Map.of("role", user.getRoles()), user.getUsername());
            Cookie cookie = new Cookie("JWT_TOKEN", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); 
            cookie.setPath("/");
            cookie.setMaxAge(jwtUtil.getExpiration().intValue());
            response.addCookie(cookie);
            return token; 
        } else {
            throw new RuntimeException("Invalid username or password");
        }
    }

    public void logout(HttpServletResponse response, String token) {
        try {
            Cookie cookie = new Cookie("JWT_TOKEN", null);
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            System.out.println(token);
        } catch (Exception e) {
            throw new RuntimeException("Failed to Logout");
        }
    }
}
