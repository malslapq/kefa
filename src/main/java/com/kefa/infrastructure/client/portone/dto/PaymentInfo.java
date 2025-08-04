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
public class PaymentInfo {

    @JsonProperty("imp_uid")
    private String impUid; // 포트원 거래고유번호
    @JsonProperty("merchant_uid")
    private String merchantUid; // 고객사 주문번호
    @JsonProperty("pay_method")
    private String payMethod; // 결제수단 구분코드
    private String channel; // 결제환경 구분코드
    @JsonProperty("pg_provider")
    private String pgProvider; // PG사 구분코드
    @JsonProperty("emb_pg_provider")
    private String embPgProvider; // 허브형결제 PG사 구분코드
    @JsonProperty("pg_tid")
    private String pgTid; // PG사 거래번호
    @JsonProperty("pg_id")
    private String pgId; // PG사 상점아이디
    private Boolean escrow; // 에스크로결제 여부
    @JsonProperty("apply_num")
    private String applyNum; // 승인번호
    @JsonProperty("bank_code")
    private String bankCode; // 은행 표준코드
    @JsonProperty("bank_name")
    private String bankName; // 은행명
    @JsonProperty("card_code")
    private String cardCode; // 카드사 코드번호
    @JsonProperty("card_name")
    private String cardName; // 카드사명
    @JsonProperty("card_issuer_code")
    private String cardIssuerCode; // 카드 발급사 코드
    @JsonProperty("card_issuer_name")
    private String cardIssuerName; // 카드 발급사명
    @JsonProperty("card_publisher_code")
    private String cardPublisherCode; // 카드 발행사 코드
    @JsonProperty("card_publisher_name")
    private String cardPublisherName; // 카드 발행사명
    @JsonProperty("card_quota")
    private Integer cardQuota; // 할부개월 수
    @JsonProperty("card_number")
    private String cardNumber; // 카드번호
    @JsonProperty("card_type")
    private Integer cardType; // 카드 구분코드
    @JsonProperty("vbank_code")
    private String vbankCode; // 가상계좌 은행 표준코드
    @JsonProperty("vbank_name")
    private String vbankName; // 가상계좌 은행명
    @JsonProperty("vbank_num")
    private String vbankNum; // 가상계좌 계좌번호
    @JsonProperty("vbank_holder")
    private String vbankHolder; // 가상계좌 예금주
    @JsonProperty("vbank_date")
    private Long vbankDate; // 가상계좌 입금기한 (Unix Timestamp)
    @JsonProperty("vbank_issued_at")
    private Long vbankIssuedAt; // 가상계좌 생성시각 (Unix Timestamp)
    private String name; // 제품명
    private BigDecimal amount; // 결제금액
    private BigDecimal cancelAmount; // 취소금액 (cancel_amount -> cancelAmount)
    private String currency; // 결제통화 구분코드
    @JsonProperty("buyer_name")
    private String buyerName; // 주문자명
    @JsonProperty("buyer_email")
    private String buyerEmail; // 주문자 Email주소
    @JsonProperty("buyer_tel")
    private String buyerTel; // 주문자 전화번호
    @JsonProperty("buyer_addr")
    private String buyerAddr; // 주문자 주소
    @JsonProperty("buyer_postcode")
    private String buyerPostcode; // 주문자 우편번호
    @JsonProperty("custom_data")
    private String customData; // 추가정보
    @JsonProperty("user_agent")
    private String userAgent; // 단말기의 UserAgent 문자열
    private String status; // 결제상태 (예: paid, cancelled)
    @JsonProperty("started_at")
    private Long startedAt; // 요청 시각 (Unix Timestamp)
    @JsonProperty("paid_at")
    private Long paidAt; // 결제 시각 (Unix Timestamp)
    @JsonProperty("failed_at")
    private Long failedAt; // 실패시각 (Unix Timestamp)
    @JsonProperty("cancelled_at")
    private Long cancelledAt; // 취소시각 (Unix Timestamp)
    @JsonProperty("fail_reason")
    private String failReason; // 결제실패 사유
    @JsonProperty("cancel_reason")
    private String cancelReason; // 결제취소 사유
    @JsonProperty("receipt_url")
    private String receiptUrl; // 매출전표 URL
    @JsonProperty("cancel_history")
    private List<PaymentCancelHistory> cancelHistory; // 취소 내역 목록
    @JsonProperty("cancel_receipt_urls")
    private List<String> cancelReceiptUrls; // 취소/부분취소 시 생성되는 취소 매출전표 확인 URL
    @JsonProperty("cash_receipt_issued")
    private Boolean cashReceiptIssued; // 현금영수증 발급 여부
    @JsonProperty("customer_uid")
    private String customerUid; // 구매자의 결제 수단 식별 고유번호
    @JsonProperty("customer_uid_usage")
    private String customerUidUsage; // 구매자의 결제 수단 식별 고유번호 사용 구분코드
    private PaymentPromotion promotion; // 프로모션 정보 (Promotion DTO 하단에 정의)

}
