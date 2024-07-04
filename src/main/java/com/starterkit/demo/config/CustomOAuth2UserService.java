package com.starterkit.demo.config;

import com.starterkit.demo.model.User;
import com.starterkit.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        if (registrationId.equals("google")) {
            return handleGoogleUser(oauth2User, userNameAttributeName);
        } else if (registrationId.equals("facebook")) {
            return handleFacebookUser(oauth2User, userNameAttributeName);
        } else {
            throw new OAuth2AuthenticationException("Unsupported registrationId: " + registrationId);
        }
    }

    protected OAuth2User handleGoogleUser(OAuth2User oauth2User, String userNameAttributeName) {
        String email = (String) oauth2User.getAttributes().get("email");
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = new User();
            user.setUsername((String) oauth2User.getAttributes().get("name"));
            user.setEmail(email);
            user.setAuthProvider(User.AuthProvider.GOOGLE);
            userRepository.save(user);
        }
        return oauth2User;
    }

    protected OAuth2User handleFacebookUser(OAuth2User oauth2User, String userNameAttributeName) {
        String email = (String) oauth2User.getAttributes().get("email");
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            user = new User();
            user.setUsername((String) oauth2User.getAttributes().get("name"));
            user.setEmail(email);
            user.setAuthProvider(User.AuthProvider.FACEBOOK);
            userRepository.save(user);
        }
        return oauth2User;
    }
}
