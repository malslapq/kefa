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
public class PortOneCancelRequest {

    @JsonProperty("imp_uid")
    private String impUid; // 포트원 거래고유번호
    @JsonProperty("merchant_uid")
    private String merchantUid; // 고객사 주문번호
    private BigDecimal amount; // (부분)취소 요청금액
    @JsonProperty("tax_free")
    private BigDecimal taxFree; // (부분)취소요청 금액 중 면세금액
    @JsonProperty("vat_amount")
    private BigDecimal vatAmount; // (부분)취소요청금액 중 부가세 금액
    private BigDecimal checksum; // 현재시점의 취소 가능한 잔액 (체크섬)
    private String reason; // 취소 사유
    @JsonProperty("refund_holder")
    private String refundHolder; // 환불계좌 예금주
    @JsonProperty("refund_bank")
    private String refundBank; // 환불계좌 은행코드
    @JsonProperty("refund_account")
    private String refundAccount; // 환불계좌 계좌번호
    @JsonProperty("refund_tel")
    private String refundTel; // 환불계좌 예금주 연락처
    @JsonProperty("retain_promotion")
    private Boolean retainPromotion; // 프로모션 정책 유지 여부

}
