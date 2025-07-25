package com.kefa.api.dto.developmentItem.request;

import com.kefa.common.type.Stage;
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
public class DevelopmentItemAddRequest {

    @NotBlank(message = "이름은 필수입니다")
    private String name;

    @NotNull(message = "단계는 필수입니다")
    private Stage stage;

    @NotBlank(message = "기술 분야는 필수입니다")
    private String technologyField;

    @NotBlank(message = "설명은 필수입니다")
    private String description;

}