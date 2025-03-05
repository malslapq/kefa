package com.kefa.api.dto.account.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDeleteRequest {

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;

    @NotBlank(message = "계정 삭제 확인을 위해 'DELETE'를 입력해주세요")
    @Pattern(regexp = "DELETE", message = "계정 삭제 확인을 위해 정확히 'DELETE'를 입력해주세요")
    private String confirm;

}
