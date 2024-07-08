/* (C)2024 */
package com.starterkit.demo.unit;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

import com.starterkit.demo.dto.NewUserRequestDTO;
import com.starterkit.demo.dto.UserResponseDTO;
import com.starterkit.demo.exception.AuthenticationException;
import com.starterkit.demo.exception.ResourceNotFoundException;
import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.model.Role;
import com.starterkit.demo.model.User;
import com.starterkit.demo.repository.UserRepository;
import com.starterkit.demo.service.RoleService;
import com.starterkit.demo.service.UserService;
import com.starterkit.demo.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceUnitTest {

    @Mock private UserRepository userRepository;

    @Mock private PasswordEncoder passwordEncoder;

    @Mock private JwtUtil jwtUtil;

    @Mock private RoleService roleService;

    @InjectMocks private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_SavesAndReturnsUser() {
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
    void login_ValidCredentials_ReturnsToken() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedpassword");
        Role role = new Role();
        role.setName(EnumRole.ROLE_USER);
        user.setRoles(Set.of(role));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedpassword")).thenReturn(true);
        when(jwtUtil.generateToken(Map.of("roles", List.of("ROLE_USER")), "testuser"))
                .thenReturn("token");

        HttpServletResponse response = new MockHttpServletResponse();
        String token = userService.login("testuser", "password", response);

        assertThat(token).isEqualTo("token");
        Cookie cookie = ((MockHttpServletResponse) response).getCookie("JWT_TOKEN");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isEqualTo("token");
    }

    @Test
    void login_InvalidCredentials_ThrowsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        HttpServletResponse response = new MockHttpServletResponse();

        assertThatThrownBy(() -> userService.login("testuser", "password", response))
                .isInstanceOf(AuthenticationException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    void deleteUser_ValidId_DeletesUser() {
        UUID userId = UUID.randomUUID();
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void getUserById_ValidId_ReturnsUser() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(userId);
        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    void getUserById_InvalidId_ThrowsException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: " + userId);
    }

    @Test
    void getUserByUsername_UserNotFound_ThrowsException() {
        String username = "nonexistentuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByUsername(username))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with username: " + username);
    }

    @Test
    void updateUser_ValidId_UpdatesUser() {
        UUID userId = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("oldusername");
        existingUser.setPassword("oldpassword");

        User updatedDetails = new User();
        updatedDetails.setUsername("newusername");
        updatedDetails.setPassword("newpassword");
        updatedDetails.setName("newname");
        updatedDetails.setEmail("newemail@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodednewpassword");
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User updatedUser = userService.updateUser(userId, updatedDetails);

        assertThat(updatedUser.getUsername()).isEqualTo("newusername");
        assertThat(updatedUser.getPassword()).isEqualTo("encodednewpassword");
        assertThat(updatedUser.getName()).isEqualTo("newname");
        assertThat(updatedUser.getEmail()).isEqualTo("newemail@example.com");
    }

    @Test
    void logout_SuccessfullyClearsCookie() {
        HttpServletResponse response = new MockHttpServletResponse();
        userService.logout(response, "token");

        Cookie cookie = ((MockHttpServletResponse) response).getCookie("JWT_TOKEN");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getValue()).isNull();
        assertThat(cookie.getMaxAge()).isEqualTo(0);
    }

    @Test
    void getAllUsers_WithNameFilter_ReturnsFilteredUserPage() {
        User user = new User();
        user.setUsername("testuser");
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findByNameContaining(anyString(), any(PageRequest.class)))
                .thenReturn(page);

        Page<User> result = userService.getAllUsers(0, 10, "test", null, "id", "asc");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    void getAllUsers_WithEmailFilter_ReturnsFilteredUserPage() {
        User user = new User();
        user.setUsername("testuser");
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findByEmailContaining(anyString(), any(PageRequest.class)))
                .thenReturn(page);

        Page<User> result = userService.getAllUsers(0, 10, null, "test@example.com", "id", "asc");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    void getAllUsers_WithNameAndEmailFilter_ReturnsFilteredUserPage() {
        User user = new User();
        user.setUsername("testuser");
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findByNameContainingAndEmailContaining(
                        anyString(), anyString(), any(PageRequest.class)))
                .thenReturn(page);

        Page<User> result = userService.getAllUsers(0, 10, "test", "test@example.com", "id", "asc");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    void getTotalCount_WithNameFilter_ReturnsCount() {
        when(userRepository.countByNameContaining(anyString())).thenReturn(1L);

        long count = userService.getTotalCount("test", null);

        assertThat(count).isEqualTo(1);
    }

    @Test
    void getTotalCount_WithEmailFilter_ReturnsCount() {
        when(userRepository.countByEmailContaining(anyString())).thenReturn(1L);

        long count = userService.getTotalCount(null, "test@example.com");

        assertThat(count).isEqualTo(1);
    }

    @Test
    void getTotalCount_WithNameAndEmailFilter_ReturnsCount() {
        when(userRepository.countByNameContainingAndEmailContaining(anyString(), anyString()))
                .thenReturn(1L);

        long count = userService.getTotalCount("test", "test@example.com");

        assertThat(count).isEqualTo(1);
    }
}
