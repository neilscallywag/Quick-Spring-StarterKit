package com.starterkit.demo.service;

import com.starterkit.demo.dto.NewUserRequestDTO;
import com.starterkit.demo.dto.RoleDTO;
import com.starterkit.demo.dto.UserResponseDTO;
import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.model.Role;
import com.starterkit.demo.model.User;
import com.starterkit.demo.repository.UserRepository;
import com.starterkit.demo.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final RoleService roleService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, RoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.roleService = roleService;
    }

    public Page<User> getAllUsers(int page, int size, String nameFilter, String emailFilter, String sortField, String sortOrder) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortField));
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
    public long getTotalCount(String nameFilter, String emailFilter) {
        if (nameFilter != null && emailFilter != null) {
            return userRepository.countByNameContainingAndEmailContaining(nameFilter, emailFilter);
        } else if (nameFilter != null) {
            return userRepository.countByNameContaining(nameFilter);
        } else if (emailFilter != null) {
            return userRepository.countByEmailContaining(emailFilter);
        } else {
            return userRepository.count();
        }
    }
    

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }
    public UserResponseDTO createUser(NewUserRequestDTO userRequestDTO) {
        userRequestDTO.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        User user = NewUserRequestDTO.toUser(userRequestDTO);
        
        Role defaultRole = roleService.findRoleByName(EnumRole.ROLE_USER);

        user.getRoles().add(defaultRole);

        User savedUser = userRepository.save(user);

        return convertToUserResponseDTO(savedUser);
    }

    private UserResponseDTO convertToUserResponseDTO(User user) {
        UserResponseDTO response = new UserResponseDTO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setDateOfBirth(user.getDateOfBirth());

        Set<RoleDTO> roleDTOs = user.getRoles().stream().map(role -> {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setId(role.getId());
            roleDTO.setName(role.getName());
            return roleDTO;
        }).collect(Collectors.toSet());
        response.setRoles(roleDTOs);

        response.setProvider(user.getProvider());
        response.setImageUrl(user.getImageUrl());
        response.setEmailVerified(user.getEmailVerified());
        response.setAuthProvider(user.getAuthProvider());

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
            Map<String, Object> claims = Map.of(
                "roles", user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toList())
            );
            String token = jwtUtil.generateToken(claims, user.getUsername());
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
        } catch (Exception e) {
            throw new RuntimeException("Failed to Logout");
        }
    }
}
