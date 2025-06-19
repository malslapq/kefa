package com.kefa.api.dto.consulting.request;

import com.kefa.common.type.FeedbackType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateConsultingFeedbackRequest {

    @NotBlank
    private String content;
    @NotNull
    private FeedbackType feedbackType;

}
