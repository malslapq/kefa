package com.kefa.api.dto.document.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddDocumentFeedbackRequest {

    @NotBlank
    private String content;

}
