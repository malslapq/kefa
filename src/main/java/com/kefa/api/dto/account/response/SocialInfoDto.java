package com.kefa.api.dto.account.response;

import com.kefa.common.type.LoginType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SocialInfoDto {

    private LoginType loginType;

}
