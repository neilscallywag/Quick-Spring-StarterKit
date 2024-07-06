package com.starterkit.demo.unit;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.Set;

class UserServiceUnitTest {

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
        when(jwtUtil.generateToken(Map.of("roles", List.of("ROLE_USER")), "testuser")).thenReturn("token");

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
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    void deleteUser_ValidId_DeletesUser() {
        UUID userId = UUID.randomUUID();
        userService.deleteUser(userId);
        verify(userRepository).deleteById(userId);
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
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
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
}
