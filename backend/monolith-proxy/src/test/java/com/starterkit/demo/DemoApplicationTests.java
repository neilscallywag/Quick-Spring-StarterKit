package com.starterkit.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.starterkit.demo.config.TestContainersConfig;
import com.starterkit.demo.config.TestDataInitializerConfig;
import com.starterkit.demo.config.TestSecurityConfig;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {TestDataInitializerConfig.class, DemoApplication.class, TestContainersConfig.class, TestSecurityConfig.class})
@SpringBootTest
@ActiveProfiles("test")
class DemoApplicationTests {

    @Test
    void contextLoads() {
        // Test context loading
    }
}
