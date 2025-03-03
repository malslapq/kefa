package com.kefa.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AccountUpdateRequestDto {

    @NotBlank(message = "공백은 불가능합니다.")
    private String name;

}
