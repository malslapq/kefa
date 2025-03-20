package com.kefa.api.dto.consulting.response;

import com.kefa.domain.entity.Feedback;
import com.kefa.domain.type.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackResponse {

    private Long id;
    private Long targetId;
    private Long accountId;
    private Long parentId;
    private String content;
    private FeedbackType feedbackType;
    private LocalDateTime createdAt;

    public static FeedbackResponse from(Feedback feedback) {
        return FeedbackResponse.builder()
            .id(feedback.getId())
            .targetId(feedback.getTargetId())
            .accountId(feedback.getAccountId())
            .parentId(feedback.getParentId())
            .content(feedback.getContent())
            .feedbackType(feedback.getFeedbackType())
            .createdAt(feedback.getCreatedAt())
            .build();
    }

}
