package com.kefa.api.dto.account.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SocialInfoDeleteResponse {

    private final LocalDateTime deletedAt = LocalDateTime.now();
    private final String message = "계정 통합 취소 성공";

}
