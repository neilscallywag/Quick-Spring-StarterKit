package com.starterkit.demo.integration;


import com.starterkit.demo.dto.UserResponseDTO;
import com.starterkit.demo.model.User;
import com.starterkit.demo.repository.UserRepository;
import com.starterkit.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateAndRetrieveUser() {
        User user = new User();
        user.setName("Test Name"); // Add this line
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
        user.setName("Test Name"); // Add this line
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("password");
    
        UserResponseDTO createdUser = userService.createUser(user);
        UUID userId = createdUser.getId();
    
        userService.deleteUser(userId);
        assertFalse(userRepository.findById(userId).isPresent());
    }
    
}
