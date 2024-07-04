package com.starterkit.demo.dto;

import lombok.Data;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import com.starterkit.demo.model.User;

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

  
}
