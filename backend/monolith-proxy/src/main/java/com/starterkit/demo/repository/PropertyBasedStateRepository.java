package com.starterkit.demo.repository;

import org.springframework.core.env.Environment;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;
import org.togglz.core.Feature;
import org.togglz.core.util.Strings;

public class PropertyBasedStateRepository implements StateRepository {

    private final Environment environment;

    public PropertyBasedStateRepository(Environment environment) {
        this.environment = environment;
    }

    @Override
    public FeatureState getFeatureState(Feature feature) {
        String propertyKey = "togglz.features." + feature.name() + ".enabled";
        String value = environment.getProperty(propertyKey);

        if (Strings.isEmpty(value)) {
            return null;
        }

        boolean enabled = Boolean.parseBoolean(value);
        FeatureState state = new FeatureState(feature);
        state.setEnabled(enabled);
        return state;
    }

    @Override
    public void setFeatureState(FeatureState featureState) {
        throw new UnsupportedOperationException("Feature states are read-only");
    }
}
