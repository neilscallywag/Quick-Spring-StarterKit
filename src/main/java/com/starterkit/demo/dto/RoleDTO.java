package com.starterkit.demo.dto;

import java.security.AuthProvider;
import java.util.UUID;

import com.starterkit.demo.model.EnumRole;

import lombok.Data;

@Data
public class RoleDTO {
    private Integer id;
    private EnumRole name;
}