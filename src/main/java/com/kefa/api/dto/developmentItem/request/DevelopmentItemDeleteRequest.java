package com.kefa.api.dto.developmentItem.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DevelopmentItemDeleteRequest {

    @NotBlank(message = "개발 아이템 삭제 확인을 위해 'DELETE'를 입력해주세요")
    @Pattern(regexp = "DELETE", message = "계정 삭제 확인을 위해 정확히 'DELETE'를 입력해주세요")
    private String confirm;

}
