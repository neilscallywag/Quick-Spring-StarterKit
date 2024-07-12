package com.starterkit.demo.util;

import com.starterkit.demo.dto.NewUserRequestDTO;
import com.starterkit.demo.dto.RoleDTO;
import com.starterkit.demo.dto.UserResponseDTO;
import com.starterkit.demo.model.Role;
import com.starterkit.demo.model.User;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(NewUserRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setName(dto.getName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setDateOfBirth(dto.getDateOfBirth());
        // user.setProvider(dto.getProvider());
        // user.setImageUrl(dto.getImageUrl());
        // user.setEmailVerified(dto.getEmailVerified());
        // user.setAuthProvider(dto.getAuthProvider());

        return user;
    }

    public UserResponseDTO toResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setRoles(user.getRoles().stream().map(this::toRoleDTO).collect(Collectors.toSet()));
        // dto.setProvider(user.getProvider());
        // dto.setImageUrl(user.getImageUrl());
        // dto.setEmailVerified(user.getEmailVerified());
        // dto.setAuthProvider(user.getAuthProvider());

        return dto;
    }

    public RoleDTO toRoleDTO(Role role) {
        if (role == null) {
            return null;
        }

        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());

        return dto;
    }

    public void updateUserFromDetails(User details, User user) {
        user.setUsername(details.getUsername());
        user.setEmail(details.getEmail());
        user.setPassword(details.getPassword());
        user.setName(details.getName());
        user.setPhoneNumber(details.getPhoneNumber());
        user.setDateOfBirth(details.getDateOfBirth());
        user.setRoles(details.getRoles());
        // user.setProvider(details.getProvider());
        // user.setProviderId(details.getProviderId());
        // user.setImageUrl(details.getImageUrl());
        // user.setEmailVerified(details.getEmailVerified());
        // user.setAuthProvider(details.getAuthProvider());
    }
}
