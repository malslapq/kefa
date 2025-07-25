package com.kefa.api.dto.account.request;

import com.kefa.common.type.SubscriptionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountUpdateSubscriptionTypeRequest {

    @NotNull(message = "구독 종류는 필수입니다")
    private SubscriptionType subscriptionType;

}
