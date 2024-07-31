/* (C)2024 */
package com.starterkit.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.starterkit.demo.config.filter.JwtAuthenticationFilter;
import com.starterkit.demo.config.filter.LogIdFilter;
import com.starterkit.demo.service.CustomUserDetailsService;
import com.starterkit.demo.util.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;

import static com.starterkit.demo.model.EnumRole.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private static final String ACTUATOR_HEALTH = "/actuator/health";
    private static final String ACTUATOR_PROMETHEUS = "/actuator/prometheus";
    private static final String API_USERS_LOGIN = "/api/users/login";
    private static final String API_USERS_REGISTER = "/api/users/register";
    private static final String API_USERS_LOGOUT = "/api/users/logout";
    private static final String PUBLIC = "/public/**";
    private static final String V3_API_DOCS = "/v3/api-docs/**";
    private static final String SWAGGER_UI_HTML = "/swagger-ui.html";
    private static final String SWAGGER_UI = "/swagger-ui/**";
    private static final String API_USERS_ME = "/api/users/me";
    private static final String API_USERS_ID = "/api/users/{id}";
    private static final String API_USERS = "/api/users";

    public static final String AUTH_TOKEN = "JWT_TOKEN";

    private final CustomUserDetailsService customUserDetailsService;
    private final LogIdFilter logIdFilter;
    private final JwtUtil jwtUtil;


    public SecurityConfig(
            CustomUserDetailsService customUserDetailsService, LogIdFilter logIdFilter,
            JwtUtil jwtUtil) {
        this.customUserDetailsService = customUserDetailsService;
        this.logIdFilter = logIdFilter;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(customUserDetailsService, jwtUtil);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(
                        sessionManager ->
                                sessionManager.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS))
                .exceptionHandling(
                        exceptionHandling ->
                                exceptionHandling.authenticationEntryPoint(
                                        (request, response, exception) ->
                                                response.sendError(
                                                        HttpServletResponse.SC_UNAUTHORIZED,
                                                        exception.getMessage())))
                .authorizeHttpRequests(
                        authorizeHttpRequest -> {
                            authorizeHttpRequest
                                    .requestMatchers(ACTUATOR_HEALTH)
                                    .permitAll()
                                    .requestMatchers(API_USERS_LOGIN)
                                    .permitAll()
                                    .requestMatchers(API_USERS_REGISTER)
                                    .permitAll()
                                    .requestMatchers(API_USERS_LOGOUT)
                                    .permitAll()
                                    .requestMatchers(PUBLIC)
                                    .permitAll();

                            // For dev modes
                            if ("dev".equals(activeProfile) || "staging".equals(activeProfile)) {
                                authorizeHttpRequest
                                        .requestMatchers(ACTUATOR_PROMETHEUS)
                                        .permitAll()
                                        .requestMatchers(V3_API_DOCS)
                                        .permitAll()
                                        .requestMatchers(SWAGGER_UI_HTML)
                                        .permitAll()
                                        .requestMatchers(SWAGGER_UI)
                                        .permitAll();
                            }

                            // RBAC

                            authorizeHttpRequest
                                    .requestMatchers(HttpMethod.GET, API_USERS_ME)
                                    .hasAnyAuthority(
                                            ROLE_USER.name(),
                                            ROLE_OFFICER.name(),
                                            ROLE_MANAGER.name())
                                    .requestMatchers(HttpMethod.GET, API_USERS_ID)
                                    .hasAnyAuthority(ROLE_OFFICER.name(), ROLE_MANAGER.name())
                                    .requestMatchers(HttpMethod.GET, API_USERS)
                                    .hasAnyAuthority(ROLE_OFFICER.name(), ROLE_MANAGER.name())
                                    .requestMatchers(HttpMethod.POST, API_USERS)
                                    .hasAuthority(ROLE_MANAGER.name())
                                    .requestMatchers(HttpMethod.PUT, API_USERS_ID)
                                    .hasAnyAuthority(ROLE_OFFICER.name(), ROLE_MANAGER.name())
                                    .requestMatchers(HttpMethod.DELETE, API_USERS_ID)
                                    .hasAuthority(ROLE_MANAGER.name())
                                    .anyRequest()
                                    .authenticated();
                        })
                .addFilterBefore(logIdFilter, AuthorizationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter(), AuthorizationFilter.class);
        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        // for getAllUsers
        config.addExposedHeader("X-Total-Count");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
