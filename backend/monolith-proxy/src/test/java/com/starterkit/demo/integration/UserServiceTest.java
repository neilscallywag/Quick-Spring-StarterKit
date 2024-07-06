package com.starterkit.demo.integration;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.starterkit.demo.dto.NewUserRequestDTO;
import com.starterkit.demo.dto.UserResponseDTO;
import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.model.Role;
import com.starterkit.demo.model.User;
import com.starterkit.demo.repository.UserRepository;
import com.starterkit.demo.service.RoleService;
import com.starterkit.demo.service.UserService;
import com.starterkit.demo.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllUsers_ReturnsUserPage() {
        User user = new User();
        user.setUsername("testuser");
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        Page<User> result = userService.getAllUsers(0, 10, null, null);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    public void createUser_SavesAndReturnsUser() {
        NewUserRequestDTO dto = new NewUserRequestDTO();
        dto.setUsername("newuser");
        dto.setPassword("password");
        dto.setName("New User");
        dto.setEmail("newuser@example.com");

        Role role = new Role();
        role.setName(EnumRole.ROLE_USER);

        when(passwordEncoder.encode("password")).thenReturn("encodedpassword");
        when(roleService.findRoleByName(EnumRole.ROLE_USER)).thenReturn(role);

        User user = NewUserRequestDTO.toUser(dto);
        user.setPassword("encodedpassword");
        user.getRoles().add(role);

        when(userRepository.save(user)).thenReturn(user);

        UserResponseDTO response = userService.createUser(dto);

        assertThat(response.getUsername()).isEqualTo("newuser");
        assertThat(response.getName()).isEqualTo("New User");
        assertThat(response.getRoles()).hasSize(1);
    }

    @Test
    public void login_ValidCredentials_ReturnsToken() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedpassword");
        Role role = new Role();
        role.setName(EnumRole.ROLE_USER);
        user.setRoles(Set.of(role));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedpassword")).thenReturn(true);
        when(jwtUtil.generateToken(Map.of("roles", List.of("ROLE_USER")), "testuser")).thenReturn("token");

        HttpServletResponse response = new MockHttpServletResponse();
        String token = userService.login("testuser", "password", response);

        assertThat(token).isEqualTo("token");
        Cookie cookie = ((MockHttpServletResponse) response).getCookie("JWT_TOKEN");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isEqualTo("token");
    }

    @Test
    public void login_InvalidCredentials_ThrowsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        HttpServletResponse response = new MockHttpServletResponse();

        assertThatThrownBy(() -> userService.login("testuser", "password", response))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid username or password");
    }
}
