package com.kefa.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AccountsSearchType {

    EMAIL("이메일"),
    NAME("이름"),
    SUBSCRIPTION_TYPE("구독 종류"),
    ROLE("권한"),
    TOTAL("전체");

    private final String type;

    public static AccountsSearchType from(String value) {
        if (value == null) return null;


        return Arrays.stream(AccountsSearchType.values())
            .filter(r -> r.name().equalsIgnoreCase(value))
            .findFirst()
            .orElse(TOTAL);
    }

}
