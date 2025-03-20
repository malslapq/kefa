package com.kefa.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("관리자"),
    EXPERT("전문가"),
    STAFF("직원"),
    FREE_ACCOUNT("회원"),
    PAID_ACCOUNT("유료 회원"),
    CONCIERGE_ACCOUNT("컨시어지");

    private final String role;
}