package com.kefa.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("관리자"),
    EXPERT("전문가"),
    STAFF("직원"),
    ACCOUNT("회원");

    private final String role;
}