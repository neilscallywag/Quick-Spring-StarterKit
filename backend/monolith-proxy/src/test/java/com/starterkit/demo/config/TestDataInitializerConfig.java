package com.starterkit.demo.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;

@Component
public class TestDataInitializerConfig {

    private final JdbcTemplate jdbcTemplate;

    public TestDataInitializerConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS users");
            jdbcTemplate.execute("CREATE TABLE users (id SERIAL PRIMARY KEY, username VARCHAR(255), password VARCHAR(255), email VARCHAR(255))");

            SimpleJdbcInsert insertUser = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("users")
                    .usingGeneratedKeyColumns("id");

            Map<String, Object> userParameters = new HashMap<>();
            userParameters.put("username", "mockUser");
            userParameters.put("password", "{noop}mockPassword");  // Using {noop} for no-op password encoder
            userParameters.put("email", "mockuser@example.com");

            insertUser.execute(userParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
