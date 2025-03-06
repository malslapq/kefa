package com.kefa.application.usecase;

import com.kefa.api.dto.company.request.CompanyAddRequest;
import com.kefa.api.dto.company.response.CompanyAddResponse;
import com.kefa.api.dto.company.response.CompanyResponse;
import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.CompanyException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.NtsException;
import com.kefa.domain.entity.Account;
import com.kefa.domain.entity.Company;
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
    @DisplayName("회사 단일 조회 성공")
    void getMyCompanySuccess() {
        //given
        Long companyId = 1L;
        Long accountId = 1L;

        Account account = Account.builder()
            .id(accountId)
            .build();

        Company company = Company.builder()
            .id(companyId)
            .name("test")
            .businessNumber("123-456-78901")
            .address("주소테스트")
            .industry("업종테스트")
            .revenueMillion(1000L)
            .account(account)  // account 설정
            .build();

        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
            .id(accountId)  // 동일한 accountId
            .build();

        when(companyRepository.findCompanyById(companyId)).thenReturn(Optional.of(company));

        //when
        CompanyResponse response = companyUseCase.getMyCompany(companyId, authenticationInfo);

        //then
        assertThat(response.getId()).isEqualTo(company.getId());
        assertThat(response.getName()).isEqualTo(company.getName());
        assertThat(response.getBusinessNumber()).isEqualTo(company.getBusinessNumber());
        assertThat(response.getAddress()).isEqualTo(company.getAddress());
        assertThat(response.getIndustry()).isEqualTo(company.getIndustry());
        assertThat(response.getRevenueMillion()).isEqualTo(company.getRevenueMillion());
    }

    @Test
    @DisplayName("회사 단일 조회 실패 - 회사가 존재하지 않음")
    void getMyCompanyFailNotFound() {
        //given
        Long companyId = 1L;
        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
            .id(1L)
            .build();

        when(companyRepository.findCompanyById(companyId)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> companyUseCase.getMyCompany(companyId, authenticationInfo))
            .isInstanceOf(CompanyException.class)
            .hasMessage(ErrorCode.COMPANY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회사 단일 조회 실패 - 권한 없음")
    void getMyCompanyFailUnauthorized() {
        //given
        Long companyId = 1L;
        Long accountId = 1L;
        Long differentAccountId = 2L;

        Company company = Company.builder()
            .id(companyId)
            .name("test")
            .account(Account.builder()
                .id(accountId)
                .build())
            .build();

        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
            .id(differentAccountId)  // 다른 계정 ID로 조회 시도
            .build();

        when(companyRepository.findCompanyById(companyId)).thenReturn(Optional.of(company));

        //when & then
        assertThatThrownBy(() -> companyUseCase.getMyCompany(companyId, authenticationInfo))
            .isInstanceOf(CompanyException.class)
            .hasMessage(ErrorCode.NOT_COMPANY_OWNER.getMessage());
    }

    @Test
    @DisplayName("회사 목록 조회 성공")
    void findAllByAccountIdSuccess() {
        //given
        Long accountId = 1L;
        List<Company> companies = List.of(
            Company.builder()
                .id(1L)
                .name("test1")
                .businessNumber("123-456-78901")
                .address("주소테스트1")
                .industry("업종테스트1")
                .revenueMillion(1000L)
                .build(),
            Company.builder()
                .id(2L)
                .name("test2")
                .businessNumber("123-456-78902")
                .address("주소테스트2")
                .industry("업종테스트2")
                .revenueMillion(2000L)
                .build()
        );

        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
            .id(accountId)
            .build();

        when(companyRepository.findAllByAccountId(accountId)).thenReturn(companies);

        //when
        List<CompanyResponse> responses = companyUseCase.getMyCompanies(authenticationInfo);

        //then
        assertNotNull(responses);
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getName()).isEqualTo(companies.get(0).getName());
        assertThat(responses.get(0).getBusinessNumber()).isEqualTo(companies.get(0).getBusinessNumber());
        assertThat(responses.get(0).getAddress()).isEqualTo(companies.get(0).getAddress());
        assertThat(responses.get(0).getIndustry()).isEqualTo(companies.get(0).getIndustry());
        assertThat(responses.get(0).getRevenueMillion()).isEqualTo(companies.get(0).getRevenueMillion());
        assertThat(responses.get(1).getName()).isEqualTo(companies.get(1).getName());
        assertThat(responses.get(1).getBusinessNumber()).isEqualTo(companies.get(1).getBusinessNumber());
        assertThat(responses.get(1).getAddress()).isEqualTo(companies.get(1).getAddress());
        assertThat(responses.get(1).getIndustry()).isEqualTo(companies.get(1).getIndustry());
        assertThat(responses.get(1).getRevenueMillion()).isEqualTo(companies.get(1).getRevenueMillion());
    }

    @Test
    @DisplayName("회사 목록 조회 - 데이터가 없는 경우 빈 리스트 반환")
    void findAllByAccountIdEmpty() {
        //given
        Long accountId = 1L;
        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
            .id(accountId)
            .build();

        when(companyRepository.findAllByAccountId(accountId)).thenReturn(Collections.emptyList());

        //when
        List<CompanyResponse> responses = companyUseCase.getMyCompanies(authenticationInfo);

        //then
        assertNotNull(responses);
        assertThat(responses).isEmpty();
    }

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
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getBusinessNumber()).isEqualTo(request.getBusinessNumber());
        assertThat(response.getAddress()).isEqualTo(request.getAddress());
        assertThat(response.getIndustry()).isEqualTo(request.getIndustry());
        assertThat(response.getRevenueMillion()).isEqualTo(request.getRevenueMillion());
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
