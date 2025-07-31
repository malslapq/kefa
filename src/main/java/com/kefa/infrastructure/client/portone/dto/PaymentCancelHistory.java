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
public class PaymentCancelHistory {

    @JsonProperty("pg_tid")
    private String pgTid; // PG사 거래번호
    private BigDecimal amount; // 취소 금액
    @JsonProperty("cancel_amount")
    private BigDecimal cancelAmount; // 취소 금액 (중복 필드? API 문서 확인 필요)
    @JsonProperty("cancel_reason")
    private String cancelReason; // 취소 사유
    @JsonProperty("cancelled_at")
    private Long cancelledAt; // 취소 시각 (Unix Timestamp)
    @JsonProperty("receipt_url")
    private String receiptUrl; // 매출전표 URL

}
