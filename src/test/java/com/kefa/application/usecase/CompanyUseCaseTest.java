package com.kefa.application.usecase;

import com.kefa.api.dto.company.request.CompanyAddRequest;
import com.kefa.api.dto.company.response.CompanyAddResponse;
import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.NtsException;
import com.kefa.domain.entity.Account;
import com.kefa.infrastructure.client.nts.BusinessNoValidateResponse;
import com.kefa.infrastructure.client.nts.BusinessStatusData;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.repository.CompanyRepository;
import com.kefa.infrastructure.security.auth.AuthenticationInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompanyUseCaseTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CompanyUseCase companyUseCase;


    @Test
    @DisplayName("회사 등록 성공")
    void addSuccess() {
        //given
        CompanyAddRequest request = CompanyAddRequest.builder()
            .name("test")
            .businessNumber("213-854-66870")
            .address("주소테스트")
            .industry("업종테스트")
            .revenueMillion(1000L)
            .build();

        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
            .id(1L)
            .build();

        Account account = Account.builder()
            .id(1L)
            .build();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        //when
        CompanyAddResponse response = companyUseCase.add(request, authenticationInfo);

        //then
        assertNotNull(response);
        assertThat(response.getName()).isEqualTo("test");
        assertThat(response.getBusinessNumber()).isEqualTo("213-854-66870");
        assertThat(response.getAddress()).isEqualTo("주소테스트");
        assertThat(response.getIndustry()).isEqualTo("업종테스트");
        assertThat(response.getRevenueMillion()).isEqualTo(1000L);
    }

    @Test
    @DisplayName("회사 등록 실패 - 회원이 존재하지 않음")
    void addFailNotFoundAccount() {
        //given
        CompanyAddRequest request = CompanyAddRequest.builder()
            .name("test")
            .businessNumber("213-854-66870")
            .address("주소테스트")
            .industry("업종테스트")
            .revenueMillion(1000L)
            .build();

        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
            .id(1L)
            .build();

        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> companyUseCase.add(request, authenticationInfo))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage(ErrorCode.ACCOUNT_NOT_FOUND.getMessage());
    }


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
