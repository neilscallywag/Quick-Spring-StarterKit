package com.starterkit.demo.integration;

import com.starterkit.demo.dto.UserResponseDTO;
import com.starterkit.demo.model.User;
import com.starterkit.demo.repository.UserRepository;
import com.starterkit.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateAndRetrieveUser() {
        User user = new User();
        user.setName("Test Name");
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");

        UserResponseDTO createdUser = userService.createUser(user);
        assertNotNull(createdUser);

        User retrievedUser = userService.getUserById(createdUser.getId());
        assertNotNull(retrievedUser);
        assertEquals("testuser", retrievedUser.getUsername());
        assertEquals("testuser@example.com", retrievedUser.getEmail());
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setName("Test Name");
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");

        UserResponseDTO createdUser = userService.createUser(user);

        user.setName("Updated Name");
        user.setUsername("updateduser");
        User updatedUser = userService.updateUser(createdUser.getId(), user);

        assertEquals("updateduser", updatedUser.getUsername());
        assertEquals("Updated Name", updatedUser.getName());
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setName("Test Name");
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");

        UserResponseDTO createdUser = userService.createUser(user);
        UUID userId = createdUser.getId();

        userService.deleteUser(userId);
        assertFalse(userRepository.findById(userId).isPresent());
    }

    // @Test
    // void testGetAllUsersWithFilters() {
    //     User user1 = new User();
    //     user1.setName("John Doe");
    //     user1.setUsername("johndoe");
    //     user1.setEmail("johndoe@example.com");
    //     user1.setPassword("password");
    //     userService.createUser(user1);

    //     User user2 = new User();
    //     user2.setName("Jane Doe");
    //     user2.setUsername("janedoe");
    //     user2.setEmail("janedoe@example.com");
    //     user2.setPassword("password");
    //     userService.createUser(user2);

    //     Page<User> usersPage = userService.getAllUsers(0, 10, "John", "example.com");
    //     assertEquals(1, usersPage.getTotalElements());
    //     assertEquals("johndoe", usersPage.getContent().get(0).getUsername());
    // }

    // @Test
    // void testGetAllUsersWithoutFilters() {
    //     User user1 = new User();
    //     user1.setName("John Doe");
    //     user1.setUsername("johndoe");
    //     user1.setEmail("johndoe@example.com");
    //     user1.setPassword("password");
    //     userService.createUser(user1);

    //     User user2 = new User();
    //     user2.setName("Jane Doe");
    //     user2.setUsername("janedoe");
    //     user2.setEmail("janedoe@example.com");
    //     user2.setPassword("password");
    //     userService.createUser(user2);

    //     Page<User> usersPage = userService.getAllUsers(0, 10, null, null);
    //     assertEquals(2, usersPage.getTotalElements());
    // }

    @Test
    void testLoginWithValidCredentials() {
        User user = new User();
        user.setName("Test Name");
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");

        userService.createUser(user);

        HttpServletResponse response = new MockHttpServletResponse();
        String token = userService.login("testuser", "password", response);

        assertNotNull(token);
        Cookie cookie = ((MockHttpServletResponse) response).getCookie("JWT_TOKEN");
        assertNotNull(cookie);
        assertEquals(token, cookie.getValue());
    }

    @Test
    void testLoginWithInvalidCredentials() {
        User user = new User();
        user.setName("Test Name");
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");

        userService.createUser(user);

        HttpServletResponse response = new MockHttpServletResponse();
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.login("testuser", "wrongpassword", response);
        });

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void testLogout() {
        User user = new User();
        user.setName("Test Name");
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");

        userService.createUser(user);

        HttpServletResponse response = new MockHttpServletResponse();
        String token = userService.login("testuser", "password", response);

        userService.logout(response, token);
        Cookie cookie = ((MockHttpServletResponse) response).getCookie("JWT_TOKEN");
        assertNotNull(cookie);
        assertEquals(3600000, cookie.getMaxAge());
    }
}
