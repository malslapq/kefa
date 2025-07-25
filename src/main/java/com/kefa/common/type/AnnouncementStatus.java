package com.kefa.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AnnouncementStatus {

    ONGOING("진행 중"),
    CLOSED("마감");

    private final String description;

}
