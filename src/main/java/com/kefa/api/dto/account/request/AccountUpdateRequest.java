package com.kefa.api.dto.account.request;

import com.kefa.common.type.Role;
import com.kefa.common.type.SubscriptionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountUpdateRequest {

    @NotBlank
    private String name;
    @Email
    @NotBlank
    private String email;
    @NotNull
    private SubscriptionType subscriptionType;
    @NotNull
    private Role role;
    private boolean emailVerified;

}
