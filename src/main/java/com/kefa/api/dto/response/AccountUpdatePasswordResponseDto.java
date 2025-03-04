package com.kefa.api.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class AccountUpdatePasswordResponseDto {

    private final LocalDateTime updateAt = LocalDateTime.now();
    private final String message = "비밀번호 변경 완료";

}
