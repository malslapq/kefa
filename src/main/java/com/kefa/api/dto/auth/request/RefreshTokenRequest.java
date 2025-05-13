package com.kefa.api.dto.auth.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequest {

    @NotEmpty(message = "리프레시 토큰은 비어 있을 수 없습니다")
    private String refreshToken;

}
