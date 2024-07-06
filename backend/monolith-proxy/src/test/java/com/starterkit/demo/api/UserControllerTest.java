// package com.starterkit.demo.api;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.starterkit.demo.DemoApplication;
// import com.starterkit.demo.config.TestContainersConfig;
// import com.starterkit.demo.config.TestSecurityConfig;
// import com.starterkit.demo.dto.LocalLoginRequestDTO;
// import com.starterkit.demo.dto.NewUserRequestDTO;
// import com.starterkit.demo.model.User;
// import com.starterkit.demo.repository.UserRepository;
// import com.starterkit.demo.service.UserService;
// import com.starterkit.demo.util.JwtUtil;
// import jakarta.servlet.http.Cookie;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.Mockito;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.context.ContextConfiguration;
// import org.springframework.test.context.junit.jupiter.SpringExtension;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

// import java.util.Optional;
// import java.util.UUID;

// import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @ExtendWith(SpringExtension.class)
// @AutoConfigureMockMvc
// @ContextConfiguration(classes = {DemoApplication.class, TestSecurityConfig.class, TestContainersConfig.class})
// @SpringBootTest
// @ActiveProfiles("test")
// class UserControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @MockBean
//     private UserService userService;

//     @MockBean
//     private UserRepository userRepository;

//     @MockBean
//     private JwtUtil jwtUtil;

//     @BeforeEach
//     void setup() {
//         // Create a mock user with all necessary details
//         User mockUser = new User();
//         mockUser.setUsername("mockUser");
//         mockUser.setPassword("{noop}mockPassword");
//         mockUser.setEmail("mockuser@example.com");
//         mockUser.setId(UUID.randomUUID()); // Assuming an ID is also required
//         // Set other necessary fields if required
    
//         // Mock the behavior of the userRepository to return this mockUser when called
//         Mockito.when(userRepository.findByUsername("mockUser")).thenReturn(Optional.of(mockUser));
//         Mockito.when(jwtUtil.isTokenExpired("dummyToken")).thenReturn(false);
//     }

//     @Test
//     @WithMockUser(username = "mockUser")
//     void testGetUsers() throws Exception {
//         mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
//                 .with(csrf())
//                 .param("page", "0")
//                 .param("size", "10"))
//                 .andExpect(status().isOk())
//                 .andExpect(header().exists(HttpHeaders.CONTENT_TYPE))
//                 .andExpect(jsonPath("$").isArray());
//     }

//     @Test
//     @WithMockUser(username = "mockUser")
//     void testGetUserById() throws Exception {
//         UUID userId = UUID.randomUUID();
//         Mockito.when(userService.getUserById(userId)).thenReturn(new User());

//         mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", userId)
//                 .with(csrf()))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//     }

//     @Test
//     @WithMockUser(username = "mockUser")
//     void testCreateUser() throws Exception {
//         NewUserRequestDTO newUser = new NewUserRequestDTO();
//         newUser.setUsername("testuser");
//         newUser.setEmail("test@example.com");
//         newUser.setPassword("password");

//         mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
//                 .with(csrf())
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(newUser)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//     }

//     @Test
//     @WithMockUser(username = "mockUser")
//     void testUpdateUser() throws Exception {
//         UUID userId = UUID.randomUUID();
//         User user = new User();
//         user.setUsername("updateduser");
//         user.setEmail("updated@example.com");
//         user.setPassword("newpassword");

//         mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", userId)
//                 .with(csrf())
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(user)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//     }

//     @Test
//     @WithMockUser(username = "mockUser")
//     void testDeleteUser() throws Exception {
//         UUID userId = UUID.randomUUID();

//         mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", userId)
//                 .with(csrf()))
//                 .andExpect(status().isNoContent());
//     }

//     @Test
//     @WithMockUser(username = "mockUser")
//     void testLogin() throws Exception {
//         LocalLoginRequestDTO loginRequest = new LocalLoginRequestDTO();
//         loginRequest.setUsername("testuser");
//         loginRequest.setPassword("password");

//         mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login")
//                 .with(csrf())
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(loginRequest)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//     }

//     @Test
//     @WithMockUser(username = "mockUser")
//     void testGetMe() throws Exception {
//         String token = "dummyToken";
//         Mockito.when(jwtUtil.isTokenExpired(token)).thenReturn(false);

//         mockMvc.perform(MockMvcRequestBuilders.get("/api/users/me")
//                 .with(csrf())
//                 .cookie(new Cookie("JWT_TOKEN", token)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON));
//     }

//     @Test
//     @WithMockUser(username = "mockUser")
//     void testLogout() throws Exception {
//         String token = "dummyToken";

//         mockMvc.perform(MockMvcRequestBuilders.post("/api/users/logout")
//                 .with(csrf())
//                 .cookie(new Cookie("JWT_TOKEN", token)))
//                 .andExpect(status().isOk());
//     }
// }
