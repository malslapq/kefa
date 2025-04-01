package com.kefa.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Stage {

    PLANNING("기획"),
    PROGRAMMING("진행 중"),
    PATENT_PENDING("특허 출원"),
    COMMERCIALIZED("상용화"),
    HOLD("보류");

    private final String stage;

}
