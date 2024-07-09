package com.starterkit.demo.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;

@Configuration
public class TestStateConfig {

    @Bean
    public StateRepository stateRepository() {
        StateRepository mockRepository = Mockito.mock(StateRepository.class);
        // Mock the getFeatureState method to return a default state
        Mockito.when(mockRepository.getFeatureState(Mockito.any())).thenReturn(new FeatureState(Mockito.any()));
        // Mock the setFeatureState method to avoid UnsupportedOperationException
        Mockito.doNothing().when(mockRepository).setFeatureState(Mockito.any());
        return mockRepository;
    }
}
