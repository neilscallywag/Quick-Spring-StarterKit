/* (C)2024 */
package com.starterkit.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.starterkit.demo.model.Role;
import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.repository.RoleRepository;
import com.starterkit.demo.service.base.BaseService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleService extends BaseService<Role, Integer> {

    private final RoleRepository roleRepository;

    @Override
    public RoleRepository getRepository() {
        return roleRepository;
    }

    public Role findRoleByName(EnumRole name) {
        return roleRepository
                .findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }
}
