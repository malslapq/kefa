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

    /**
     * Returns the corresponding {@code NoticesSearchType} for the given string value.
     *
     * If the input matches either the enum's Korean label or its name (case-insensitive), the matching constant is returned.
     * If no match is found, {@code TOTAL} is returned as the default.
     *
     * @param value the string to match against the enum's label or name
     * @return the matching {@code NoticesSearchType}, or {@code TOTAL} if no match is found
     */
    public static NoticesSearchType from(String value) {

        return Arrays.stream(values())
            .filter(type -> type.type.equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value))
            .findFirst()
            .orElse(TOTAL);
    }

}
