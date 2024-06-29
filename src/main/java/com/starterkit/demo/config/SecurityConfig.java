package com.starterkit.demo.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import jakarta.servlet.http.HttpServletResponse;

// Indicates that this class contains one or more @Bean methods and may be processed by the Spring container to generate bean definitions and service requests at runtime
@Configuration
// Enables Spring Security’s web security support and provides the Spring MVC integration
@EnableWebSecurity
public class SecurityConfig {
    @Value("${spring.profiles.active:}")
    private String activeProfile;


    // Defines a bean for AuthenticationManager to handle authentication, which is needed by Spring Security
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Configures the security filter chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults()) // Enables Cross-Origin Resource Sharing (CORS) with default configuration
            .csrf(csrf -> csrf.disable()) // Disables Cross-Site Request Forgery (CSRF) protection
            .sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Configures session management to be stateless
            .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(
                (request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage()) // Sets the response status to 401 Unauthorized if authentication fails
            ))
            .authorizeHttpRequests(authorizeHttpRequest -> {
                authorizeHttpRequest
                .requestMatchers("/public/**").permitAll();

                // if dev env or staging allow actuator and openapi endpoints
                if ("dev".equals(activeProfile) || "staging".equals(activeProfile)) {
                    authorizeHttpRequest.requestMatchers("/actuator/**").permitAll()
                    .requestMatchers("/v3/api-docs/**").permitAll()
                    .requestMatchers("/swagger-ui.html").permitAll()
                    .requestMatchers("/swagger-ui/**").permitAll();
                }
                
                // everything else 401 if not authn
                authorizeHttpRequest.anyRequest().authenticated();
            });
        return http.build(); 
    }

    // Configures a CORS filter to handle CORS preflight requests and allow cross-origin requests
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // Creates a new source for URL-based CORS configuration
        CorsConfiguration config = new CorsConfiguration(); // Creates a new CORS configuration object
        config.setAllowCredentials(true); // Allows credentials (cookies, authorization headers, or TLS client certificates) to be included in requests
        config.addAllowedOriginPattern("*"); // Allows all origins to access the resources
        config.addAllowedHeader("*"); // Allows all headers to be included in requests
        config.addAllowedMethod("*"); // Allows all HTTP methods (GET, POST, etc.) to be used in requests
        source.registerCorsConfiguration("/**", config); // Registers the CORS configuration for all URL patterns
        return new CorsFilter(source); // Returns a new CorsFilter with the configured source
    }
}
