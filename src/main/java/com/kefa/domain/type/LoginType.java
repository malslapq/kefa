package com.kefa.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginType {

    GOOGLE("구글"),
    KAKAO("카카오");

    private final String type;

}
