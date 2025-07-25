package com.kefa.api.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordResetDto {

    @NotBlank(message = "비밀번호는 필수입니다")
    private String newPassword;
    @NotBlank(message = "비밀번호 확인은 필수입니다")
    private String confirmPassword;
    @NotBlank(message = "비밀번호 초기화 토큰은 필수입니다")
    private String token;

}
