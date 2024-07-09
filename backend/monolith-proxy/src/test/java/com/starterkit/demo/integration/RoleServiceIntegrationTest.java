package com.starterkit.demo.integration;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

import com.starterkit.demo.config.TestStateConfig;
import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.model.Role;
import com.starterkit.demo.repository.RoleRepository;
import com.starterkit.demo.service.RoleService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;
@ContextConfiguration(classes = {TestStateConfig.class})

 class RoleServiceIntegrationTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void findRoleByName_RoleExists_ReturnsRole() {
        Role role = new Role();
        role.setName(EnumRole.ROLE_USER);

        when(roleRepository.findByName(EnumRole.ROLE_USER)).thenReturn(Optional.of(role));

        Role foundRole = roleService.findRoleByName(EnumRole.ROLE_USER);

        assertThat(foundRole.getName()).isEqualTo(EnumRole.ROLE_USER);
    }

    @Test
     void findRoleByName_RoleDoesNotExist_ThrowsException() {
        when(roleRepository.findByName(EnumRole.ROLE_USER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.findRoleByName(EnumRole.ROLE_USER))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Role not found");
    }
}
