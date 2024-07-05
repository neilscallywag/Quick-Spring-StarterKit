// package com.starterkit.demo.api;

// import com.starterkit.demo.AppConfig;
// import com.starterkit.demo.TestSecurityConfig;
// import com.starterkit.demo.controller.UserController;
// import com.starterkit.demo.dto.LocalLoginRequestDTO;
// import com.starterkit.demo.dto.MeResponseDTO;
// import com.starterkit.demo.dto.UserResponseDTO;
// import com.starterkit.demo.exception.ResourceNotFoundException;
// import com.starterkit.demo.model.User;
// import com.starterkit.demo.service.UserService;
// import com.starterkit.demo.util.JwtUtil;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.ClaimsBuilder;
// import io.jsonwebtoken.ClaimsMutator;
// import io.jsonwebtoken.Jwts;
// import jakarta.servlet.http.Cookie;
// import jakarta.servlet.http.HttpServletResponse;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.context.annotation.Import;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.http.MediaType;
// import org.springframework.test.context.TestPropertySource;
// import org.springframework.test.web.servlet.MockMvc;

// import java.util.Collections;
// import java.util.Date;
// import java.util.List;
// import java.util.UUID;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.doNothing;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @SpringBootTest
// @Import({TestSecurityConfig.class})
// @AutoConfigureMockMvc
// @AutoConfigureJsonTesters
// @TestPropertySource(properties = {
//         "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration," +
//                                       "org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration"
// })
// class UserControllerTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private UserService userService;

//     @MockBean
//     private JwtUtil jwtUtil;

//     @Test
//     void testGetUsers() throws Exception {
//         Page<User> userPage = new PageImpl<>(Collections.emptyList());
//         when(userService.getAllUsers(0, 10, null, null)).thenReturn(userPage);

//         mockMvc.perform(get("/api/users"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$").isEmpty());
//     }

//     @Test
//     void testGetUsers_WithFilters() throws Exception {
//         User user = new User();
//         user.setUsername("testuser");
//         Page<User> userPage = new PageImpl<>(List.of(user));

//         when(userService.getAllUsers(0, 10, "testuser", "test@example.com"))
//                 .thenReturn(userPage);

//         mockMvc.perform(get("/api/users")
//                         .param("nameFilter", "testuser")
//                         .param("emailFilter", "test@example.com"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$[0].username").value("testuser"));
//     }


//     @Test
//     void testGetUserById() throws Exception {
//         UUID userId = UUID.randomUUID();
//         User user = new User();
//         user.setId(userId);

//         when(userService.getUserById(userId)).thenReturn(user);

//         mockMvc.perform(get("/api/users/{id}", userId))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.id").value(userId.toString()));
//     }

//     @Test
//     void testGetUserById_NotFound() throws Exception {
//         UUID userId = UUID.randomUUID();

//         when(userService.getUserById(userId)).thenThrow(new ResourceNotFoundException("User not found"));

//         mockMvc.perform(get("/api/users/{id}", userId))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     void testCreateUser() throws Exception {
//         UserResponseDTO userResponseDTO = new UserResponseDTO();
//         userResponseDTO.setUsername("testuser");
//         userResponseDTO.setEmail("testuser@example.com");

//         when(userService.createUser(any(User.class))).thenReturn(userResponseDTO);

//         mockMvc.perform(post("/api/users")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content("{ \"username\": \"testuser\", \"email\": \"testuser@example.com\", \"password\": \"password\" }"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.username").value("testuser"))
//                 .andExpect(jsonPath("$.email").value("testuser@example.com"));
//     }

//     @Test
//     void testCreateUser_InvalidRequest() throws Exception {
//         mockMvc.perform(post("/api/users")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content("{ \"username\": \"\", \"email\": \"\", \"password\": \"\" }"))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     void testUpdateUser() throws Exception {
//         UUID userId = UUID.randomUUID();
//         User user = new User();
//         user.setUsername("updateduser");
//         user.setEmail("updateduser@example.com");
//         user.setPassword("newpassword");

//         when(userService.updateUser(any(UUID.class), any(User.class))).thenReturn(user);

//         mockMvc.perform(put("/api/users/{id}", userId)
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content("{ \"username\": \"updateduser\", \"email\": \"updateduser@example.com\", \"password\": \"newpassword\" }"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.username").value("updateduser"))
//                 .andExpect(jsonPath("$.email").value("updateduser@example.com"));
//     }

//     @Test
//     void testUpdateUser_InvalidRequest() throws Exception {
//         UUID userId = UUID.randomUUID();

//         mockMvc.perform(put("/api/users/{id}", userId)
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content("{ \"username\": \"\", \"email\": \"\", \"password\": \"\" }"))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     void testDeleteUser() throws Exception {
//         UUID userId = UUID.randomUUID();
//         doNothing().when(userService).deleteUser(userId);

//         mockMvc.perform(delete("/api/users/{id}", userId))
//                 .andExpect(status().isNoContent());
//     }

//     @Test
//     void testDeleteUser_NotFound() throws Exception {
//         UUID userId = UUID.randomUUID();
//         doNothing().when(userService).deleteUser(userId);

//         mockMvc.perform(delete("/api/users/{id}", userId))
//                 .andExpect(status().isNoContent());
//     }

//     @Test
//     void testLogin() throws Exception {
//         LocalLoginRequestDTO loginRequest = new LocalLoginRequestDTO();
//         loginRequest.setUsername("testuser");
//         loginRequest.setPassword("password");

//         when(userService.login(anyString(), anyString(), any(HttpServletResponse.class))).thenReturn("login-token");

//         mockMvc.perform(post("/api/users/login")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content("{ \"username\": \"testuser\", \"password\": \"password\" }"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("login-token"));
//     }

//     @Test
//     void testLogin_InvalidRequest() throws Exception {
//         mockMvc.perform(post("/api/users/login")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content("{ \"username\": \"\", \"password\": \"\" }"))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     void testLogout() throws Exception {
//         doNothing().when(userService).logout(any(HttpServletResponse.class), anyString());

//         mockMvc.perform(post("/api/users/logout")
//                         .cookie(new Cookie("JWT_TOKEN", "token")))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("Logged out successfully"));
//     }

//     @Test
//     void testLogout_InvalidRequest() throws Exception {
//         mockMvc.perform(post("/api/users/logout"))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     void testGetMe() throws Exception {
//         when(jwtUtil.isTokenExpired(anyString())).thenReturn(false);
//         when(jwtUtil.getClaimsFromToken(anyString())).thenReturn(createMockClaims());

//         mockMvc.perform(post("/api/users/me")
//                         .cookie(new Cookie("JWT_TOKEN", "valid-token")))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.username").value("testuser"))
//                 .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
//     }

//     @Test
//     void testGetMe_ExpiredToken() throws Exception {
//         when(jwtUtil.isTokenExpired(anyString())).thenReturn(true);

//         mockMvc.perform(post("/api/users/me")
//                         .cookie(new Cookie("JWT_TOKEN", "expired-token")))
//                 .andExpect(status().isBadRequest());
//     }

//     @Test
//     void testGetMe_NoToken() throws Exception {
//         mockMvc.perform(post("/api/users/me"))
//                 .andExpect(status().isBadRequest());
//     }

//     private Claims createMockClaims() {
//         ClaimsBuilder claims = Jwts.claims();
//         claims.subject("testuser");
//         claims.add("role", List.of("ROLE_USER"));
//         claims.issuedAt(new Date(System.currentTimeMillis()));
//         claims.expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60));
//         return claims.build();
//     }
// }
