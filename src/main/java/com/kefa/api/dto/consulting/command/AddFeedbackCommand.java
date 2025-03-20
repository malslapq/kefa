package com.kefa.api.dto.consulting.command;

import com.kefa.api.dto.consulting.request.AddFeedbackRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddFeedbackCommand {

    private Long companyId;
    private Long consultingSessionId;
    private Long loginAccountId;
    private AddFeedbackRequest request;

}
