package com.kefa.infrastructure.security.auth;

import com.kefa.application.usecase.AuthenticationUseCase;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.vo.AccountVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AuthenticationUseCase authenticationUseCase;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        String socialProvider = userRequest.getClientRegistration().getRegistrationId();
        String email = getEmailFromOauth2User(oAuth2User, socialProvider);

        AccountVO accountVO = authenticationUseCase.authenticateSocialUser(email, socialProvider);

        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("accountId", accountVO.getId());

        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority("ROLE_" + accountVO.getRole())),
            attributes,
            email
        );

    }

    private String getEmailFromOauth2User(OAuth2User oauth2User, String registrationId) {

        return switch (registrationId) {
            case "google" -> oauth2User.getAttribute("email");
            case "kakao" -> {
                Map<String, Object> kakaoAccount = oauth2User.getAttribute("kakao_account");
                yield (String) Objects.requireNonNull(kakaoAccount).get("email");
            }
            default -> throw new OAuth2AuthenticationException(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER.getMessage());
        };

    }

}
