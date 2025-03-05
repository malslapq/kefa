package com.kefa.api.dto.account.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class AccountDeleteResponse {

    private final LocalDateTime deletedAt = LocalDateTime.now();
    private final String message = "계정 탈퇴 성공";
    private String email;

}
