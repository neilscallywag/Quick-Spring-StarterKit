package com.starterkit.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.FeatureManagerBuilder;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.user.NoOpUserProvider;
import org.togglz.core.user.UserProvider;

import com.starterkit.demo.features.FeatureToggle;
import com.starterkit.demo.repository.PropertyBasedStateRepository;


@Configuration
public class TogglzConfig {

    @Bean
    public FeatureManager featureManager(Environment environment) {
        return new FeatureManagerBuilder()
                .featureEnum(FeatureToggle.class)
                .stateRepository(stateRepository(environment))
                .userProvider(userProvider())
                .build();
    }

    @Bean
    public StateRepository stateRepository(Environment environment) {
        return new PropertyBasedStateRepository(environment);
    }
    @Bean
    public UserProvider userProvider() {
    // Use a no-op user provider, which means no specific user context is needed
    // AKA user-specific feature toggles are not required.

        return new NoOpUserProvider();
    }
}
