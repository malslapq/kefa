package com.kefa.api.dto.account.request;

import com.kefa.common.type.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountUpdateRoleRequest {

    @NotNull
    private Role role;

}
