package com.kefa.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Stage {

    PLANNING("기획"),
    PROGRAMMING("진행 중"),
    COMPLETE("완료"),
    HOLD("보류");

    private final String description;

}
