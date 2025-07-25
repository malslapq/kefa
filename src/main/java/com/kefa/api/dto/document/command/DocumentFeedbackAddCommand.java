package com.kefa.api.dto.document.command;

import com.kefa.infrastructure.security.auth.LoginAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentFeedbackAddCommand {

    private String content;
    private Long consultingSessionId;
    private Long documentId;
    private LoginAccount loginAccount;

}
