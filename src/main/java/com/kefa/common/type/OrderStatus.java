package com.kefa.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    COMPLETED("COMPLETED");

    private final String status;

}
