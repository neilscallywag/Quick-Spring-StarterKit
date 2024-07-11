/* (C)2024 */
package com.starterkit.demo.service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.starterkit.demo.dto.NewUserRequestDTO;
import com.starterkit.demo.dto.RoleDTO;
import com.starterkit.demo.dto.UserResponseDTO;
import com.starterkit.demo.exception.AuthenticationException;
import com.starterkit.demo.exception.InvalidRequestException;
import com.starterkit.demo.exception.ResourceNotFoundException;
import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.model.Role;
import com.starterkit.demo.model.User;
import com.starterkit.demo.repository.UserRepository;
import com.starterkit.demo.service.base.BaseService;
import com.starterkit.demo.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService extends BaseService<User, UUID> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RoleService roleService;
    @Lazy
    private final UserService self;

    @Override
    public UserRepository getRepository() {
        return userRepository;
    }

    public Page<User> getAllUsers(
            int page,
            int size,
            String nameFilter,
            String emailFilter,
            String sortField,
            String sortOrder) {
        try {
            Pageable pageable =
                    PageRequest.of(
                            page, size, Sort.by(Sort.Direction.fromString(sortOrder), sortField));
            if (nameFilter != null && emailFilter != null) {
                return userRepository.findByNameContainingAndEmailContaining(
                        nameFilter, emailFilter, pageable);
            } else if (nameFilter != null) {
                return userRepository.findByNameContaining(nameFilter, pageable);
            } else if (emailFilter != null) {
                return userRepository.findByEmailContaining(emailFilter, pageable);
            } else {
                return userRepository.findAll(pageable);
            }
        } catch (Exception e) {
            log.error("Failed to retrieve users", e);
            throw new InvalidRequestException("Failed to retrieve users");
        }
    }

    public long getTotalCount(String nameFilter, String emailFilter) {
        try {
            if (nameFilter != null && emailFilter != null) {
                return userRepository.countByNameContainingAndEmailContaining(
                        nameFilter, emailFilter);
            } else if (nameFilter != null) {
                return userRepository.countByNameContaining(nameFilter);
            } else if (emailFilter != null) {
                return userRepository.countByEmailContaining(emailFilter);
            } else {
                return userRepository.count();
            }
        } catch (Exception e) {
            log.error("Failed to get user count", e);
            throw new InvalidRequestException("Failed to get user count");
        }
    }

    public User getUserById(UUID id) {
        return this.findById(id);
    }

    public User getUserByUsername(String username) {
        try {
            return userRepository
                    .findByUsername(username)
                    .orElseThrow(
                            () ->
                                    new ResourceNotFoundException(
                                            "User not found with username: " + username));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to get user by username", e);
            throw new InvalidRequestException("Failed to get user by username");
        }
    }

    public UserResponseDTO createUser(NewUserRequestDTO userRequestDTO) {
        try {
            if (userRepository.findByUsername(userRequestDTO.getUsername()).isPresent()) {
                throw new InvalidRequestException("Username is already taken.");
            }
            if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
                throw new InvalidRequestException("Email is already taken.");
            }

            userRequestDTO.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
            User user = NewUserRequestDTO.toUser(userRequestDTO);

            Role defaultRole = roleService.findRoleByName(EnumRole.ROLE_USER);

            user.getRoles().add(defaultRole);

            User savedUser = self.create(user);

            return convertToUserResponseDTO(savedUser);
        } catch (InvalidRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to create user", e);
            throw new InvalidRequestException("Failed to create user");
        }
    }

    private UserResponseDTO convertToUserResponseDTO(User user) {
        try {
            UserResponseDTO response = new UserResponseDTO();
            response.setId(user.getId());
            response.setUsername(user.getUsername());
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setPhoneNumber(user.getPhoneNumber());
            response.setDateOfBirth(user.getDateOfBirth());

            Set<RoleDTO> roleDTOs =
                    user.getRoles().stream()
                            .map(
                                    role -> {
                                        RoleDTO roleDTO = new RoleDTO();
                                        roleDTO.setId(role.getId());
                                        roleDTO.setName(role.getName());
                                        return roleDTO;
                                    })
                            .collect(Collectors.toSet());
            response.setRoles(roleDTOs);

            response.setProvider(user.getProvider());
            response.setImageUrl(user.getImageUrl());
            response.setEmailVerified(user.getEmailVerified());
            response.setAuthProvider(user.getAuthProvider());

            return response;
        } catch (Exception e) {
            log.error("Failed to convert user to UserResponseDTO", e);
            throw new InvalidRequestException("Failed to convert user to response DTO");
        }
    }

    public User updateUser(UUID id, User userDetails) {
        try {
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
            return self.update(user);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to update user", e);
            throw new InvalidRequestException("Failed to update user");
        }
    }

    public void deleteUser(UUID id) {
        self.delete(id);
    }

    public String login(String username, String password, HttpServletResponse response) {
        try {
            User user =
                    userRepository
                            .findByUsername(username)
                            .orElseThrow(
                                    () ->
                                            new AuthenticationException(
                                                    "Invalid username or password"));
            if (passwordEncoder.matches(password, user.getPassword())) {
                Map<String, Object> claims =
                        Map.of(
                                "roles",
                                user.getRoles().stream()
                                        .map(role -> role.getName().name())
                                        .collect(Collectors.toList()));
                String token = jwtUtil.generateToken(claims, user.getUsername());
                Cookie cookie = new Cookie("JWT_TOKEN", token);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(jwtUtil.getExpiration().intValue());
                response.addCookie(cookie);
                return token;
            } else {
                throw new AuthenticationException("Invalid username or password");
            }
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to login", e);
            throw new AuthenticationException("Failed to login");
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
            log.warn("Failed to log out: " + token, e);
            throw new AuthenticationException("Failed to logout");
        }
    }
}
