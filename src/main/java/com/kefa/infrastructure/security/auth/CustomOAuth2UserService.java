package com.kefa.infrastructure.security.auth;

import com.kefa.application.usecase.AuthenticationUseCase;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Account;
import com.kefa.domain.entity.SocialInfo;
import com.kefa.domain.type.LoginType;
import com.kefa.domain.vo.AccountVO;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.repository.SocialInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final String LINK = "link";
    private static final String ROLE_PREFIX = "ROLE_";
    private final AuthenticationUseCase authenticationUseCase;
    private final AccountRepository accountRepository;
    private final SocialInfoRepository socialInfoRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        String socialProvider = userRequest.getClientRegistration().getRegistrationId();
        String email = getEmailFromOauth2User(oAuth2User, socialProvider);
        String nameAttributeKey = switch (socialProvider) {
            case "google" -> "sub";
            case "kakao" -> "id";
            default -> throw new OAuth2AuthenticationException(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER.getMessage());
        };

        // 소셜 고유 아이디
        String providerUserId = getProviderUserIdFromOauth2User(oAuth2User, socialProvider);
        boolean existsSocialUser = socialInfoRepository.existsByProviderUserIdAndLoginType(providerUserId, LoginType.valueOf(socialProvider.toUpperCase()));

        AccountVO accountVO;

        // 계정 통합인지 체크하는 변수
        String state = Optional.ofNullable(userRequest.getAdditionalParameters().get("state"))
            .map(Object::toString)
            .orElse("login");

        // 계정 통합일 경우 기존 로그인한 계정과 연결
        if (LINK.equals(state)) {

            // 중복되는 소셜 아이디 있을 경우 예외 처리
            if (existsSocialUser) {
                throw new OAuth2AuthenticationException(ErrorCode.ALREADY_PROVIDER_UER_ID.getMessage());
            }

            // 로그인한 회원 정보 가져와서 통합
            LoginAccount loginAccount = (LoginAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Account account = accountRepository.findById(loginAccount.getId()).orElseThrow(() -> new OAuth2AuthenticationException(ErrorCode.NOT_FOUND_ACCOUNT.getMessage()));
            SocialInfo socialInfo = SocialInfo.builder()
                .loginType(LoginType.valueOf(socialProvider.toUpperCase()))
                .providerUserId(providerUserId)
                .account(account)
                .build();

            socialInfoRepository.save(socialInfo);

            // defaultOauth2User 초기화 매개변수
            accountVO = AccountVO.from(account);
            attributes.put("accountId", accountVO.getId());

            /*
            계정 통합이 아닐 경우
            Oauth2 계정으로 로그인 회원가입 or 기존 계정와 통합된 계정 분기
            */
        } else {

            // 통합된 계정이 있는 경우
            if (existsSocialUser) {

                Account account = socialInfoRepository.findByProviderUserId(providerUserId)
                    .orElseThrow(() -> new OAuth2AuthenticationException(ErrorCode.NOT_FOUND_SOCIAL_USER.getMessage())).getAccount();

                accountVO = AccountVO.from(account);

                // 없는 경우 소셜 로그인 or 회원가입
            } else {
                accountVO = authenticationUseCase.authenticateSocialUser(email);
            }

        }

        attributes.put("accountId", accountVO.getId());
        attributes.put("email", email);

        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority(ROLE_PREFIX + accountVO.getRole())),
            attributes,
            nameAttributeKey
        );

    }

    private String getProviderUserIdFromOauth2User(OAuth2User oauth2User, String registrationId) {

        return switch (registrationId) {
            case "google" -> Objects.requireNonNull(oauth2User.getAttribute("sub")).toString();
            case "kakao" -> Objects.requireNonNull(oauth2User.getAttribute("id")).toString();
            default -> throw new OAuth2AuthenticationException(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER.getMessage());
        };
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
