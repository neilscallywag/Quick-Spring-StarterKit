package com.starterkit.demo.dto;

import com.starterkit.demo.model.EnumRole;

import lombok.Data;

@Data
public class RoleDTO {
    private Integer id;
    private EnumRole name;
}