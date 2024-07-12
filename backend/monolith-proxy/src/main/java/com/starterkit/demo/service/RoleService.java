/* (C)2024 */
package com.starterkit.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.starterkit.demo.model.Role;
import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role findRoleByName(EnumRole name) {
        return roleRepository
                .findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }
}
