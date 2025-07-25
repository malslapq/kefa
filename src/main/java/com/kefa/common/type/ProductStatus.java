package com.kefa.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProductStatus {

    ACTIVE("판매 중"),
    INACTIVE("판매 중단");

    private final String description;

}
