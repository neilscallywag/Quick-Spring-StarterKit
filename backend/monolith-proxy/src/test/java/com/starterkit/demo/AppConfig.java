package com.starterkit.demo;

import com.starterkit.demo.config.TestContainersConfig;
import com.starterkit.demo.config.TestDataInitializerConfig;
import com.starterkit.demo.config.TestSecurityConfig;
import com.starterkit.demo.util.JwtUtil;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import({ 
TestDataInitializerConfig.class, TestContainersConfig.class, TestSecurityConfig.class})
public class AppConfig {

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }
}
