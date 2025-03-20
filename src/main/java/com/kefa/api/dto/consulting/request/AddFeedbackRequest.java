package com.kefa.api.dto.consulting.request;

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

    private Long parentId;

    @NotBlank(message = "내용은 비어 있을 수 없습니다")
    private String content;

}
