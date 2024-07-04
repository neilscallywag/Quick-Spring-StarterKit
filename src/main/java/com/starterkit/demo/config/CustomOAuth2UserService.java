package com.starterkit.demo.config;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        ClientRegistration.ProviderDetails providerDetails = clientRegistration.getProviderDetails();

        if (providerDetails == null) {
            throw new OAuth2AuthenticationException("ProviderDetails is null for registrationId: " + clientRegistration.getRegistrationId());
        }
    

        if (registrationId == null) {
            throw new OAuth2AuthenticationException("RegistrationId is null");
        }
        
    
        switch (registrationId) {
            case "google":
                return handleGoogleUser(oauth2User);
            case "facebook":
                return handleFacebookUser(oauth2User);
            default:
                throw new OAuth2AuthenticationException("Unsupported registrationId: " + registrationId);
        }
    }
    
    protected OAuth2User handleGoogleUser(OAuth2User oauth2User) {
        // Custom logic for Google user
        return oauth2User;
    }

    protected OAuth2User handleFacebookUser(OAuth2User oauth2User) {
        // Custom logic for Facebook user
        return oauth2User;
    }
}
