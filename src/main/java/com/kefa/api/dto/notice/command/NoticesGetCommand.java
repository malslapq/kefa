package com.kefa.api.dto.notice.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NoticesGetCommand {
    private String keyword;
    private String searchType;
    private int page;
    private int size;
}
