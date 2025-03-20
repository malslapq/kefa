package com.kefa.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeedbackType {

    SESSION("세션"),
    DOCUMENT("문서");

    private final String type;

}
