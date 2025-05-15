package com.kefa.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AnnouncementsSearchType {

    TITLE("제목"),
    TAG("태그"),
    TOTAL("전체");

    private final String type;

    public static AnnouncementsSearchType from(String value) {

        return Arrays.stream(values())
            .filter(type -> type.type.equalsIgnoreCase(value))
            .findFirst()
            .orElse(TOTAL);
    }

}
