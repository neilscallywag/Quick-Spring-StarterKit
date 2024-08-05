package com.starterkit.demo.dto;

import com.starterkit.demo.model.EnumRole;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RoleDTO {
	private Integer id;
	private EnumRole name;
}
