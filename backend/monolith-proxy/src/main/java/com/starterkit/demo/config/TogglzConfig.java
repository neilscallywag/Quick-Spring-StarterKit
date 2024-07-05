package com.starterkit.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.FeatureManagerBuilder;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.user.NoOpUserProvider;
import org.togglz.core.user.UserProvider;

import com.starterkit.demo.features.FeatureToggle;

import org.togglz.core.repository.mem.InMemoryStateRepository;

@Configuration
public class TogglzConfig {

    @Bean
    public FeatureManager featureManager() {
        return new FeatureManagerBuilder()
                .featureEnum(FeatureToggle.class)
                .stateRepository(stateRepository())
                .userProvider(userProvider())
                .build();
    }

    @Bean
    public StateRepository stateRepository() {
        return new InMemoryStateRepository();
    }

    @Bean
    public UserProvider userProvider() {
    // Use a no-op user provider, which means no specific user context is needed
    // AKA user-specific feature toggles are not required.

        return new NoOpUserProvider();
    }
}
