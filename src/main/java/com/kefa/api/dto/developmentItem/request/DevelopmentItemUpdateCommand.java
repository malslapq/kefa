package com.kefa.api.dto.developmentItem.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class DevelopmentItemUpdateCommand {

    private Long companyId;
    private Long itemId;
    private DevelopmentItemUpdateRequest request;
    private Long accountId;

}
