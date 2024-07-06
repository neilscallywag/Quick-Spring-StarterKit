package com.starterkit.demo.config;

import com.starterkit.demo.service.CustomUserDetailsService;
import com.starterkit.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.servlet.http.HttpServletResponse;

import static com.starterkit.demo.model.EnumRole.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil, customUserDetailsService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(
                        sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint((request, response,
                        exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage())))
                .authorizeHttpRequests(authorizeHttpRequest -> {
                    authorizeHttpRequest
                            .requestMatchers("/actuator/health").permitAll()
                            .requestMatchers("/api/users/login").permitAll()
                            .requestMatchers("/api/users/register").permitAll()
                            .requestMatchers("/api/users/logout").permitAll()
                            .requestMatchers("/public/**").permitAll();

                    // For dev modes
                    if ("dev".equals(activeProfile) || "staging".equals(activeProfile)) {
                        authorizeHttpRequest
                                .requestMatchers("/v3/api-docs/**").permitAll()
                                .requestMatchers("/swagger-ui.html").permitAll()
                                .requestMatchers("/swagger-ui/**").permitAll();
                    }

                    authorizeHttpRequest
                            .requestMatchers(HttpMethod.GET, "/api/users/me")
                            .hasAnyAuthority(ROLE_USER.name(), ROLE_OFFICER.name(), ROLE_MANAGER.name())
                            .requestMatchers(HttpMethod.GET, "/api/users/{id}")
                            .hasAnyAuthority(ROLE_OFFICER.name(), ROLE_MANAGER.name())
                            .requestMatchers(HttpMethod.POST, "/api/users").hasAuthority(ROLE_MANAGER.name())
                            .requestMatchers(HttpMethod.PUT, "/api/users/{id}")
                            .hasAnyAuthority(ROLE_OFFICER.name(), ROLE_MANAGER.name())
                            .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasAuthority(ROLE_MANAGER.name())
                            .anyRequest().authenticated();
                })
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
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
