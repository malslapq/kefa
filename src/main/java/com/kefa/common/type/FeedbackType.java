package com.kefa.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FeedbackType {

    TAX("세무"),
    FINANCE("재무"),
    LABOR("노무"),
    GOVERNMENT("정부"),
    MANAGEMENT("경영"),
    TOTAL("종합");

    private final String type;

}
