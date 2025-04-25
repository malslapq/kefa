package com.kefa.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SearchType {

    TITLE("제목"),
    TAG("태그"),
    TOTAL("전체");

    private final String type;

    public static SearchType from(String value) {

        return Arrays.stream(values())
            .filter(type -> type.type.equalsIgnoreCase(value))
            .findFirst()
            .orElse(TOTAL);
    }

}
