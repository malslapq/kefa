package com.kefa.infrastructure.security.auth;

import com.kefa.application.usecase.AccountUseCase;
import com.kefa.application.usecase.AuthenticationUseCase;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.OAuth2Exception;
import com.kefa.common.type.LoginType;
import com.kefa.domain.vo.AccountVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final String LINK = "link";
    private final AuthenticationUseCase authenticationUseCase;
    private final AccountUseCase accountUseCase;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        String socialProvider = userRequest.getClientRegistration().getRegistrationId();
        LoginType loginType = LoginType.from(socialProvider);
        String email = getEmailFromOauth2User(oAuth2User, loginType.toString());

        // 소셜 고유 아이디
        String providerUserId = getProviderUserIdFromOauth2User(oAuth2User, loginType);
        // 계정 통합인지 체크하는 변수
        String state = Optional.ofNullable(userRequest.getAdditionalParameters().get("state"))
            .map(Object::toString)
            .orElse("login");

        AccountVO accountVO = LINK.equals(state) ?
            // 계정 통합일 경우 기존 로그인한 계정과 연결
            accountUseCase.linkAccount(providerUserId, loginType) :
            // 통합 로그인 or 회원가입 및 소셜 로그인
            authenticationUseCase.loginOrSignUp(providerUserId, email, loginType);

        return authenticationUseCase.createDefaultOauth2User(accountVO, loginType, attributes);

    }

    private String getProviderUserIdFromOauth2User(OAuth2User oauth2User, LoginType loginType) {
        return Objects.requireNonNull(oauth2User.getAttribute(loginType.getNameAttributeKey())).toString();
    }

    private String getEmailFromOauth2User(OAuth2User oauth2User, String socialProvider) {

        return switch (socialProvider) {
            case "GOOGLE" -> oauth2User.getAttribute("email");
            case "KAKAO" -> {
                Map<String, Object> kakaoAccount = oauth2User.getAttribute("kakao_account");
                yield (String) Objects.requireNonNull(kakaoAccount).get("email");
            }
            default -> throw new OAuth2Exception(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
        };

    }

}
