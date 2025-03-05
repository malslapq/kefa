package com.kefa.application.usecase;

import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.NtsException;
import com.kefa.infrastructure.client.nts.BusinessNoValidateResponse;
import com.kefa.infrastructure.client.nts.BusinessStatusData;
import com.kefa.infrastructure.repository.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CompanyUseCaseTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyUseCase companyUseCase;

    @Test
    @DisplayName("사업자번호가 정상일 경우 성공")
    void validateBusinessNumberSuccess() {
        // given
        BusinessStatusData data = BusinessStatusData.builder()
            .businessStatus("계속사업자")
            .build();

        BusinessNoValidateResponse response = BusinessNoValidateResponse.builder()
            .data(List.of(data))
            .build();

        // when & then
        assertDoesNotThrow(() -> companyUseCase.validateBusinessNumber(response));
    }

    @Test
    @DisplayName("사업자번호가 존재하지 않을 경우 BUSINESS_NUMBER_NOT_FOUND 예외")
    void validateBusinessNumberEmptyData() {
        // given
        BusinessNoValidateResponse response = BusinessNoValidateResponse.builder()
            .data(Collections.emptyList())
            .build();

        // when & then
        NtsException exception = assertThrows(NtsException.class,
            () -> companyUseCase.validateBusinessNumber(response));
        assertEquals(ErrorCode.BUSINESS_NUMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("사업자번호가 미등록 상태일 경우 BUSINESS_NUMBER_NOT_FOUND 예외")
    void validateBusinessNumberNotRegistered() {
        // given
        BusinessStatusData data = BusinessStatusData.builder()
            .taxType("국세청에 등록되지 않은 사업자등록번호입니다.")
            .build();

        BusinessNoValidateResponse response = BusinessNoValidateResponse.builder()
            .data(List.of(data))
            .build();

        // when & then
        NtsException exception = assertThrows(NtsException.class,
            () -> companyUseCase.validateBusinessNumber(response));
        assertEquals(ErrorCode.BUSINESS_NUMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("사업자번호가 휴/폐업 상태일 경우 INACTIVE_BUSINESS_NUMBER 예외")
    void validateBusinessNumberInactive() {
        // given
        BusinessStatusData data = BusinessStatusData.builder()
            .businessStatus("폐업자")
            .build();

        BusinessNoValidateResponse response = BusinessNoValidateResponse.builder()
            .data(List.of(data))
            .build();

        // when & then
        NtsException exception = assertThrows(NtsException.class,
            () -> companyUseCase.validateBusinessNumber(response));
        assertEquals(ErrorCode.INACTIVE_BUSINESS_NUMBER, exception.getErrorCode());
    }
}
