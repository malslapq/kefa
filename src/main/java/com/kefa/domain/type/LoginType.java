package com.kefa.domain.type;

import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.OAuth2Exception;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum LoginType {

    GOOGLE("구글", "sub", "google"),
    KAKAO("카카오", "id", "kakao");

    private final String type;
    private final String nameAttributeKey;
    private final String registrationId;

    public static LoginType from(String registrationId) {
        return Arrays.stream(LoginType.values())
            .filter(loginType -> loginType.registrationId.equalsIgnoreCase(registrationId))
            .findFirst()
            .orElseThrow(() -> new OAuth2Exception(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER));
    }

}
