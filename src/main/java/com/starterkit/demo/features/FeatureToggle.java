package com.starterkit.demo.features;

import org.togglz.core.Feature;
import org.togglz.core.annotation.EnabledByDefault;
import org.togglz.core.annotation.Label;

public enum FeatureToggle implements Feature {

    @Label("New Feature")
    NEW_FEATURE,

    @EnabledByDefault
    @Label("Another Feature")
    ANOTHER_FEATURE,

    @Label("Enable Google OAuth2")
    GOOGLE_OAUTH2,

    @Label("Enable Facebook OAuth2")
    FACEBOOK_OAUTH2;
}
