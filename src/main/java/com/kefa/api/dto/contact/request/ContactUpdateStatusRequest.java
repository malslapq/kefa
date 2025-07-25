package com.kefa.api.dto.contact.request;

import com.kefa.common.type.ContactStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ContactUpdateStatusRequest {

    @NotNull(message = "변경할 상담의 상태는 필수입니다.")
    private ContactStatus status;

}
