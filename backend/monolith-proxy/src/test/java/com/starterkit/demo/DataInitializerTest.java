package com.starterkit.demo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataInitializerTest {

    private final JdbcTemplate jdbcTemplate;

    public DataInitializerTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        try {
            jdbcTemplate.execute("DROP TABLE IF EXISTS roles");
            jdbcTemplate.execute("CREATE TABLE roles (id SERIAL PRIMARY KEY, name VARCHAR(255))");

            SimpleJdbcInsert insertActor = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("roles")
                    .usingGeneratedKeyColumns("id");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("name", "ROLE_USER");

            insertActor.execute(parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
