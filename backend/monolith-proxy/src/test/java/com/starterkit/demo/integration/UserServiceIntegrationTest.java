/* (C)2024 */
package com.starterkit.demo.integration;

import java.util.List;
import java.util.Optional;
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
import org.springframework.test.context.ContextConfiguration;

import com.starterkit.demo.config.TestStateConfig;
import com.starterkit.demo.exception.ResourceNotFoundException;
import com.starterkit.demo.model.User;
import com.starterkit.demo.repository.UserRepository;
import com.starterkit.demo.service.RoleService;
import com.starterkit.demo.service.UserService;
import com.starterkit.demo.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ContextConfiguration(classes = {TestStateConfig.class})

class UserServiceIntegrationTest {

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

    @Test
    void updateUser_WithExistingUser_ReturnsUpdatedUser() {
        UUID userId = UUID.randomUUID();
        User userDetails = new User();
        userDetails.setUsername("updateduser");
        userDetails.setPassword("updatedpassword");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("olduser");
        existingUser.setPassword("oldpassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("updatedpassword")).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User updatedUser = userService.updateUser(userId, userDetails);

        assertThat(updatedUser.getUsername()).isEqualTo("updateduser");
        assertThat(updatedUser.getPassword()).isEqualTo("encodedpassword");
    }

    @Test
    void getUserById_UserNotFound_ThrowsException() {
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
    void deleteUser_UserDeletedSuccessfully() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).delete(user);
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
}
