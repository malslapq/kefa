package com.kefa.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionType {

    FREE("무료"),
    PREMIUM("프리미엄"),
    CONCIERGE("컨시어지");

    private final String type;
}
