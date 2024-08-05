package com.starterkit.demo.config;

import com.starterkit.demo.MockJwtAuthenticationFilter;
import com.starterkit.demo.service.CustomUserDetailsService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

	private final CustomUserDetailsService userService;

	public TestSecurityConfig(CustomUserDetailsService userService) {
		this.userService = userService;
	}

	@Bean
	@Primary
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(Customizer.withDefaults())
				.csrf(csrf -> csrf.disable())
				.sessionManagement(sessionManager -> sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(mockJwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
				.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());
		return http.build();
	}

	@Bean
	@Primary
	public MockJwtAuthenticationFilter mockJwtAuthenticationFilter() {
		return new MockJwtAuthenticationFilter(userService);
	}


}
