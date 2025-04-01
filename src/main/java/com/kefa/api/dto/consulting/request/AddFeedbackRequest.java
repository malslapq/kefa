package com.kefa.api.dto.consulting.request;

import com.kefa.domain.type.FeedbackType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddFeedbackRequest {

    @NotNull(message = "대상 ID는 비어 있을 수 없습니다")
    private Long targetId;

    @NotNull(message = "계정 ID는 비어 있을 수 없습니다")
    private Long accountId;

    @NotBlank(message = "내용은 비어 있을 수 없습니다")
    private String content;

    @NotNull(message = "피드백 타입은 비어 있을 수 없습니다")
    private FeedbackType feedbackType; // Enum 타입 필드 추가

}
