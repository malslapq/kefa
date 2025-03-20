package com.kefa.api.dto.consulting.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateDocNameRequest {

    @NotBlank(message = "변경 할 이름은 공백일 수 없습니다")
    private String name;

}
