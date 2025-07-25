package com.kefa.api.dto.consulting.command;

import com.kefa.api.dto.consulting.request.ConsultingFeedbackAddRequest;
import com.kefa.infrastructure.security.auth.LoginAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsultingFeedbackAddCommand {

    private Long companyId;
    private Long consultingSessionId;
    private LoginAccount loginAccount;
    private ConsultingFeedbackAddRequest request;

}
