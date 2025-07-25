package com.kefa.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum NoticesSearchType {

    TITLE("제목"),
    CONTENT("내용"),
    WRITER("작성자"),
    TOTAL("전체");

    private final String type;

    public static NoticesSearchType from(String value) {

        return Arrays.stream(values())
            .filter(type -> type.type.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value))
            .findFirst()
            .orElse(TOTAL);
    }

}
