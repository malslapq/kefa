package com.kefa.infrastructure.client.portone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PortOnePaymentBalanceResponse {

    private int code;
    private String message;
    private PaymentBalanceData response;

}
