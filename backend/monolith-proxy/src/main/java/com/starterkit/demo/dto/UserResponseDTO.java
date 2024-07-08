/* (C)2024 */
package com.starterkit.demo.dto;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.model.User;

import lombok.Data;

@Data
public class UserResponseDTO {

    private UUID id;
    private String username;
    private String name;
    private String email;
    private String phoneNumber;
    private Date dateOfBirth;
    private Set<RoleDTO> roles;
    private String provider;
    private String imageUrl;
    private Boolean emailVerified;
    private User.AuthProvider authProvider;
    private Date createdAt;
    private Date updatedAt;

    public static UserResponseDTO convertToUserResponseDTO(User user) {
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
                                    roleDTO.setName(EnumRole.valueOf(role.getName().name()));
                                    return roleDTO;
                                })
                        .collect(Collectors.toSet());
        response.setRoles(roleDTOs);
        response.setProvider(user.getProvider());
        response.setImageUrl(user.getImageUrl());
        response.setEmailVerified(user.getEmailVerified());
        response.setAuthProvider(user.getAuthProvider());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
