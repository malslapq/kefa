package com.kefa.api.dto.document.response;

import com.kefa.domain.entity.DocumentFeedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentFeedbackResponse {

    private Long id;
    private Long accountId;
    private String name;
    private String content;

    public static DocumentFeedbackResponse from(DocumentFeedback documentFeedback) {
        return DocumentFeedbackResponse.builder()
            .id(documentFeedback.getId())
            .accountId(documentFeedback.getAccountId())
            .name(documentFeedback.getName())
            .content(documentFeedback.getContent())
            .build();
    }

}
