package com.kefa.infrastructure.client.portone.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentBalanceDetail {

    @JsonProperty("tax_free")
    private BigDecimal taxFree;
    private BigDecimal supply;
    private BigDecimal vat;
    private BigDecimal service;

}
