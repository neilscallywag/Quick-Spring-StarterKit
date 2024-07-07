package com.starterkit.demo.features;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;

public enum FeatureToggle implements Feature {
    
    @Label("Enable Profiling")
    ENABLE_PROFILING,

    @Label("Send Profiling Data to Prometheus")
    SEND_PROFILING_DATA_TO_PROMETHEUS;
}
