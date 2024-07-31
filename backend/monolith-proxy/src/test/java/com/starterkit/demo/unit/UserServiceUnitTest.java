/* (C)2024 */
package com.starterkit.demo.unit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.starterkit.demo.dto.NewUserRequestDTO;
import com.starterkit.demo.dto.UserResponseDTO;
import com.starterkit.demo.event.TransactionEventListener.TransactionEvent;
import com.starterkit.demo.exception.AuthenticationException;
import com.starterkit.demo.exception.InvalidRequestException;
import com.starterkit.demo.exception.ResourceNotFoundException;
import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.model.Role;
import com.starterkit.demo.model.User;
import com.starterkit.demo.repository.UserRepository;
import com.starterkit.demo.service.LockStrategy;
import com.starterkit.demo.service.RoleService;
import com.starterkit.demo.service.UserService;
import com.starterkit.demo.util.CookieUtils;
import com.starterkit.demo.util.JwtUtil;
import com.starterkit.demo.util.UserMapper;

import jakarta.servlet.http.HttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceUnitTest {

    @Mock private UserRepository userRepository;

    @Mock private PasswordEncoder passwordEncoder;

    @Mock private RoleService roleService;

    @Mock private UserMapper userMapper;

    @Mock private ApplicationEventPublisher applicationEventPublisher;

    @Mock private LockStrategy lockStrategy;

    @Mock private CookieUtils cookieUtils;

    @Mock private JwtUtil jwtUtil;

    @InjectMocks private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        Page<User> users = new PageImpl<>(List.of(new User()));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(users);

        Page<User> result = userService.getAllUsers(0, 10, null, null, "username", "asc");

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testGetAllUsersWithFilters() {
        Page<User> users = new PageImpl<>(List.of(new User()));
        when(userRepository.findByNameContainingAndEmailContaining(
                        anyString(), anyString(), any(Pageable.class)))
                .thenReturn(users);

        Page<User> result = userService.getAllUsers(0, 10, "name", "email", "username", "asc");

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testGetTotalCount() {
        when(userRepository.count()).thenReturn(10L);

        long count = userService.getTotalCount(null, null);

        assertThat(count).isEqualTo(10);
    }

    @Test
    void testGetTotalCountWithFilters() {
        when(userRepository.countByNameContainingAndEmailContaining(anyString(), anyString()))
                .thenReturn(5L);

        long count = userService.getTotalCount("name", "email");

        assertThat(count).isEqualTo(5);
    }

    @Test
    void testGetUserById() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserById(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
    }

    @Test
    void testGetUserByIdNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(userId.toString());
    }

    @Test
    void testGetUserByUsername() {
        String username = "testUser";
        User user = new User();
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        User result = userService.getUserByUsername(username);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(username);
    }

    @Test
    void testGetUserByUsernameNotFound() {
        String username = "testUser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByUsername(username))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(username);
    }

    @Test
    void testCreateUser() {
        NewUserRequestDTO requestDTO = new NewUserRequestDTO();
        requestDTO.setUsername("newUser");
        requestDTO.setPassword("password");
        requestDTO.setEmail("newUser@example.com");
        User user = new User();
        user.setId(UUID.randomUUID());
        Role role = new Role();
        role.setName(EnumRole.ROLE_USER);
        when(passwordEncoder.encode(requestDTO.getPassword())).thenReturn("encodedPassword");
        when(userMapper.toEntity(requestDTO)).thenReturn(user);
        when(roleService.findRoleByName(EnumRole.ROLE_USER)).thenReturn(role);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(new UserResponseDTO());

        UserResponseDTO responseDTO = userService.createUser(requestDTO);

        assertThat(responseDTO).isNotNull();
        verify(applicationEventPublisher, times(1)).publishEvent(any(TransactionEvent.class));
    }

    @Test
    void testCreateUserWithExistingUsername() {
        NewUserRequestDTO requestDTO = new NewUserRequestDTO();
        requestDTO.setUsername("existingUser");
        requestDTO.setEmail("newUser@example.com");
        when(userRepository.findByUsername(requestDTO.getUsername()))
                .thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.createUser(requestDTO))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Username is already taken.");
    }

    @Test
    void testCreateUserWithExistingEmail() {
        NewUserRequestDTO requestDTO = new NewUserRequestDTO();
        requestDTO.setUsername("newUser");
        requestDTO.setEmail("existingEmail@example.com");
        when(userRepository.findByEmail(requestDTO.getEmail())).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.createUser(requestDTO))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Email is already taken.");
    }

    @Test
    void testUpdateUser() {
        UUID userId = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(userId);
        User updatedDetails = new User();
        updatedDetails.setUsername("updatedUser");
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(userMapper.toResponseDTO(existingUser)).thenReturn(new UserResponseDTO());

        UserResponseDTO responseDTO = userService.updateUser(userId, updatedDetails);

        assertThat(responseDTO).isNotNull();
        verify(applicationEventPublisher, times(1)).publishEvent(any(TransactionEvent.class));
    }

    @Test
    void testUpdateUserNotFound() {
        UUID userId = UUID.randomUUID();
        User updatedDetails = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, updatedDetails))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(userId.toString());
    }

    @Test
    void testDeleteUser() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository, times(1)).delete(user);
        verify(applicationEventPublisher, times(1)).publishEvent(any(TransactionEvent.class));
    }

    @Test
    void testDeleteUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(userId.toString());
    }

    // @Test
    // void testLoginSuccess() {
    //     String username = "testUser";
    //     String password = "password";
    //     User user = new User();
    //     user.setUsername(username);
    //     user.setPassword("encodedPassword");
    //     HttpServletResponse response = mock(HttpServletResponse.class);
    //     when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    //     when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
    //     String token = userService.login(username, password, response);

    //     assertThat(token).isNotNull();
    //     assertThat(jwtUtil.getUserNameFromToken(token)).isEqualTo(username);
    //     verify(cookieUtils, times(1))
    //             .createCookie(anyString(), anyString(), anyInt(),
    // any(HttpServletResponse.class));
    // }

    @Test
    void testLoginFailure() {
        String username = "testUser";
        String password = "password";
        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> userService.login(username, password, response))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid username or password");
    }

    @Test
    void testLogout() {
        HttpServletResponse response = mock(HttpServletResponse.class);

        userService.logout(response);

        verify(cookieUtils, times(1)).clearCookie(any(HttpServletResponse.class), anyString());
    }
}
