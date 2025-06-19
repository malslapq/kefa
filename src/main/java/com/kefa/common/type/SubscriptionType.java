package com.kefa.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SubscriptionType {

    FREE("무료"),
    PREMIUM("프리미엄"),
    CONCIERGE("컨시어지");

    private final String type;

    public static SubscriptionType from(String value) {
        if (value == null) return null;

        return Arrays.stream(SubscriptionType.values())
            .filter(s -> s.name().equalsIgnoreCase(value))
            .findFirst()
            .orElse(null);
    }

}
