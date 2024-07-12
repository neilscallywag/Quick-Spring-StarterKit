package com.starterkit.demo.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

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
import com.starterkit.demo.util.JwtUtil;
import com.starterkit.demo.util.UserMapper;

class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RoleService roleService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private LockStrategy lockStrategy;

    @InjectMocks
    private UserService userService;

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
    void testGetTotalCount() {
        when(userRepository.count()).thenReturn(10L);

        long count = userService.getTotalCount(null, null);

        assertThat(count).isEqualTo(10);
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
    void testFindByIdWithLock() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        when(lockStrategy.lockUser(userId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(user);

        User result = userService.findByIdWithLock(userId, LockModeType.PESSIMISTIC_WRITE);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
    }

    @Test
    void testUpdateWithLock() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        when(lockStrategy.updateUserWithLock(user, LockModeType.PESSIMISTIC_WRITE)).thenReturn(user);

        User result = userService.updateWithLock(user, LockModeType.PESSIMISTIC_WRITE);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
    }

    @Test
    void testLogin() {
        String username = "testUser";
        String password = "password";
        User user = new User();
        user.setUsername(username);
        user.setPassword("encodedPassword");
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(anyMap(), eq(username))).thenReturn("token");

        String token = userService.login(username, password, response);

        assertThat(token).isEqualTo("token");
        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    void testLoginWithInvalidCredentials() {
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

        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    void testValidateUserRequestThrowsInvalidRequestException() {
        NewUserRequestDTO requestDTO = new NewUserRequestDTO();
        requestDTO.setUsername("existingUser");
        requestDTO.setEmail("existingEmail@example.com");
        when(userRepository.findByUsername(requestDTO.getUsername())).thenReturn(Optional.of(new User()));
        when(userRepository.findByEmail(requestDTO.getEmail())).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.createUser(requestDTO))
            .isInstanceOf(InvalidRequestException.class)
            .hasMessageContaining("Username is already taken.");

        requestDTO.setUsername("newUser");
        assertThatThrownBy(() -> userService.createUser(requestDTO))
            .isInstanceOf(InvalidRequestException.class)
            .hasMessageContaining("Email is already taken.");
    }


    @Test
    void testFuzzCreateUser() {
        NewUserRequestDTO requestDTO = new NewUserRequestDTO();
        requestDTO.setUsername(generateRandomString(255));
        requestDTO.setPassword(generateRandomString(255));
        requestDTO.setEmail(generateRandomString(255));
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.toEntity(any())).thenReturn(user);
        when(roleService.findRoleByName(EnumRole.ROLE_USER)).thenReturn(new Role());
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toResponseDTO(any())).thenReturn(new UserResponseDTO());

        UserResponseDTO responseDTO = userService.createUser(requestDTO);

        assertThat(responseDTO).isNotNull();
    }

    @Test
    void testSecurityLoginWithSqlInjection() {
        String maliciousUsername = "'; DROP TABLE users; --";
        String password = "password";
        HttpServletResponse response = mock(HttpServletResponse.class);

        assertThatThrownBy(() -> userService.login(maliciousUsername, password, response))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    private String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt((int) (Math.random() * characters.length())));
        }
        return result.toString();
    }
}
