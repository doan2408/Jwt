package com.example.jwt.Services;

import com.example.jwt.Enity.User;
import com.example.jwt.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Gọi DefaultOAuth2UserService để lấy thông tin user từ Google/Facebook
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerUserId;

        if ("google".equals(provider)) {
            providerUserId = oAuth2User.getAttribute("sub"); // google id
        } else {
            providerUserId = oAuth2User.getAttribute("id"); // facebook id
        }

        String email = oAuth2User.getAttribute("email");

        // find database or create new
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setUsername(email);
                    newUser.setProvider(provider.toUpperCase());
                    newUser.setEnabled(true);
                    return userRepository.save(newUser);
                });
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                "email"
        );
    }
}
