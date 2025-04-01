package com.kefa.api.dto.consulting.response;

import com.kefa.domain.entity.ConsultingSessionFeedback;
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
public class ConsultingSessionFeedbackDto {

    private Long id;
    private Long accountId;
    private String name;
    private String content;
    private FeedbackType feedbackType;
    private LocalDateTime createdAt;

    public static ConsultingSessionFeedbackDto from(ConsultingSessionFeedback consultingSessionFeedback) {
        return ConsultingSessionFeedbackDto.builder()
            .id(consultingSessionFeedback.getId())
            .accountId(consultingSessionFeedback.getAccountId())
            .name(consultingSessionFeedback.getName())
            .content(consultingSessionFeedback.getContent())
            .feedbackType(consultingSessionFeedback.getFeedbackType())
            .createdAt(consultingSessionFeedback.getCreatedAt())
            .build();
    }

}
