package com.starterkit.demo.unit;

import com.starterkit.demo.dto.LocalLoginRequestDTO;
import com.starterkit.demo.dto.RoleDTO;
import com.starterkit.demo.dto.UserResponseDTO;
import com.starterkit.demo.model.EnumRole;
import com.starterkit.demo.model.Role;
import com.starterkit.demo.model.User;
import com.starterkit.demo.repository.UserRepository;
import com.starterkit.demo.service.UserService;
import com.starterkit.demo.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // @Test
    // void testGetAllUsers() {
    //     List<User> users = Arrays.asList(new User(), new User());
    //     when(userRepository.findAll()).thenReturn(users);

    //     List<User> result = userService.getAllUsers();

    //     assertNotNull(result);
    //     assertEquals(2, result.size());
    // }

    @Test
    void testGetUserById() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(userId);

        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
    }

    @Test
    void testCreateUser() {
        User user = new User();
        user.setPassword("plainPassword");

        Role role = new Role();
        role.setId(1);
        role.setName(EnumRole.ROLE_USER);
        user.setRoles(Set.of(role));

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals("encodedPassword", user.getPassword());
    }

    @Test
    void testUpdateUser() {
        UUID userId = UUID.randomUUID();
        User existingUser = new User();
        existingUser.setId(userId);

        User userDetails = new User();
        userDetails.setPassword("newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User updatedUser = userService.updateUser(userId, userDetails);

        assertNotNull(updatedUser);
        assertEquals("encodedNewPassword", updatedUser.getPassword());
    }

    @Test
    void testDeleteUser() {
        UUID userId = UUID.randomUUID();

        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testLoginSuccess() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
        user.setRoles(Set.of(new Role(1, EnumRole.ROLE_USER)));

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(any(Map.class), eq("testUser"))).thenReturn("testToken");

        String token = userService.login("testUser", "plainPassword", response);

        assertNotNull(token);
        assertEquals("testToken", token);
        verify(response, times(1)).addCookie(any(Cookie.class));
    }

    @Test
    void testLoginFailure() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedPassword");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.login("testUser", "wrongPassword", response);
        });

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testLogout() {
        doNothing().when(response).addCookie(any(Cookie.class));

        userService.logout(response, "testToken");

        verify(response, times(1)).addCookie(any(Cookie.class));
    }

        @Test
        void testFuzzLoginInputs() {
            String[] fuzzValues = {
                "", " ", "admin' --", "<script>alert(1)</script>", "admin@example.com", "password123", UUID.randomUUID().toString()
            };
            
            for (String value : fuzzValues) {
                LocalLoginRequestDTO loginRequest = new LocalLoginRequestDTO();
                loginRequest.setUsername(value);
                loginRequest.setPassword(value);
                try {
                    userService.login(loginRequest.getUsername(), loginRequest.getPassword(), response);
                    // If no exception is thrown, the input should be checked further.
                    // Here we can assume a successful login returns a non-null token, which should not be the case for invalid inputs.
                    fail("Expected an exception for input: " + value);
                } catch (RuntimeException e) {
                    // Expected behavior: Invalid inputs should cause a RuntimeException
                    assertTrue(e.getMessage().contains("Invalid username or password"), 
                               "Unexpected error message for input: " + value);
                }
            }
        }
}
