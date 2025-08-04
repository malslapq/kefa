package com.kefa.infrastructure.client.portone.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentBalanceHistory {

    @JsonProperty("cash_receipt")
    private PaymentBalanceDetail cashReceipt;
    private PaymentBalanceDetail primary;
    private PaymentBalanceDetail secondary;
    private PaymentBalanceDetail discount;
    private Long created;

}
