package com.starterkit.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.starterkit.demo.clients.slack.SlackAlertClient;
import com.starterkit.demo.config.TestContainersConfig;
import com.starterkit.demo.config.TestDataInitializerConfig;
import com.starterkit.demo.config.TestSecurityConfig;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {
    TestDataInitializerConfig.class,
    TestContainersConfig.class,
    TestSecurityConfig.class,
    DemoApplication.class,
    AppConfig.class
})
@SpringBootTest
@ActiveProfiles("test")
class DemoApplicationTests {
    @MockBean
    private SlackAlertClient slackAlertClient;


    @Test
    void contextLoads() {
        // Test context loading
    }
}
