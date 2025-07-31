package com.kefa.infrastructure.client.portone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortOneCancelResponse {

    private Integer code;
    private String message;
    private PaymentInfo response;

}
