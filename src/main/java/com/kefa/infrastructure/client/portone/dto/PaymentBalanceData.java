package com.kefa.infrastructure.client.portone.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentBalanceData {

    private BigDecimal amount;
    @JsonProperty("cash_receipt")
    private PaymentBalanceDetail cashReceipt;
    private PaymentBalanceDetail primary;
    private PaymentBalanceDetail secondary;
    private PaymentBalanceDetail discount;
    @JsonProperty("histories")
    private List<PaymentBalanceHistory> histories;

}
