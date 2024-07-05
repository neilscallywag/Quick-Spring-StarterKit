package com.starterkit.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;


@AutoConfigureMockMvc
@ContextConfiguration(classes = {DemoApplication.class})
@SpringBootTest
@ActiveProfiles("test")
 class DemoApplicationTests {
    @Test
    void contextLoads() {
		// 
    }
}

