package com.kefa.api.dto.account.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AccountUpdateRequest {

    @NotBlank(message = "공백은 불가능합니다.")
    private String name;

}
