package com.kefa.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("관리자"),
    EXPERT("전문가"),
    STAFF("직원"),
    FREE_ACCOUNT("회원"),
    PAID_ACCOUNT("유료 회원"),
    CONCIERGE_ACCOUNT("컨시어지");

    private final String description;

    /**
     * Returns the {@code Role} enum constant that matches the given string value, ignoring case.
     *
     * @param value the name of the role to match, case-insensitive
     * @return the matching {@code Role} constant, or {@code null} if no match is found or if {@code value} is {@code null}
     */
    public static Role from(String value) {
        if (value == null) return null;

        return Arrays.stream(Role.values())
            .filter(r -> r.name().equalsIgnoreCase(value))
            .findFirst()
            .orElse(null);
    }
}