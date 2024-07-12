package com.starterkit.demo.unit;

import com.starterkit.demo.dto.NewUserRequestDTO;
import com.starterkit.demo.dto.RoleDTO;
import com.starterkit.demo.dto.UserResponseDTO;
import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.model.Role;
import com.starterkit.demo.model.User;
import com.starterkit.demo.util.UserMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperUnitTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void testToEntity() {
        NewUserRequestDTO dto = new NewUserRequestDTO();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("password");
        dto.setName("Test User");
        dto.setPhoneNumber("1234567890");
        dto.setDateOfBirth(null);

        User user = userMapper.toEntity(dto);

        assertNotNull(user);
        assertEquals(dto.getUsername(), user.getUsername());
        assertEquals(dto.getEmail(), user.getEmail());
        assertEquals(dto.getPassword(), user.getPassword());
        assertEquals(dto.getName(), user.getName());
        assertEquals(dto.getPhoneNumber(), user.getPhoneNumber());
        assertEquals(dto.getDateOfBirth(), user.getDateOfBirth());
    }

    @Test
    void testToEntity_NullDto() {
        User user = userMapper.toEntity(null);
        assertNull(user);
    }

    @Test
    void testToResponseDTO() {
        User user = new User();
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPhoneNumber("1234567890");
        user.setDateOfBirth(null);

        Role role = new Role();
        Integer roleId = 1;
        role.setId(roleId);
        role.setName(EnumRole.ROLE_USER);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        UserResponseDTO dto = userMapper.toResponseDTO(user);

        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getUsername(), dto.getUsername());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getPhoneNumber(), dto.getPhoneNumber());
        assertEquals(user.getDateOfBirth(), dto.getDateOfBirth());
        assertEquals(1, dto.getRoles().size());
        RoleDTO roleDTO = dto.getRoles().iterator().next();
        assertEquals(role.getId(), roleDTO.getId());
        assertEquals(role.getName(), roleDTO.getName());
    }

    @Test
    void testToResponseDTO_NullUser() {
        UserResponseDTO dto = userMapper.toResponseDTO(null);
        assertNull(dto);
    }

    @Test
    void testToRoleDTO() {
        Role role = new Role();
        Integer roleId = 1;
        role.setId(roleId);
        role.setName(EnumRole.ROLE_USER);

        RoleDTO dto = userMapper.toRoleDTO(role);

        assertNotNull(dto);
        assertEquals(role.getId(), dto.getId());
        assertEquals(role.getName(), dto.getName());
    }

    @Test
    void testToRoleDTO_NullRole() {
        RoleDTO dto = userMapper.toRoleDTO(null);
        assertNull(dto);
    }

    @Test
    void testUpdateUserFromDetails() {
        User details = new User();
        details.setUsername("updateduser");
        details.setEmail("updated@example.com");
        details.setPassword("newpassword");
        details.setName("Updated User");
        details.setPhoneNumber("0987654321");
        details.setDateOfBirth(null);

        User user = new User();
        userMapper.updateUserFromDetails(details, user);

        assertEquals(details.getUsername(), user.getUsername());
        assertEquals(details.getEmail(), user.getEmail());
        assertEquals(details.getPassword(), user.getPassword());
        assertEquals(details.getName(), user.getName());
        assertEquals(details.getPhoneNumber(), user.getPhoneNumber());
        assertEquals(details.getDateOfBirth(), user.getDateOfBirth());
    }
}
