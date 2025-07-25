package com.kefa.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConsultingStatus {

    WAITING("대기"),
    IN_PROGRESS("진행 중"),
    COMPLETED("완료");

    private final String description;

}
