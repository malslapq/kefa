package com.kefa.application.usecase;

import com.kefa.api.dto.company.request.CompanyAddRequest;
import com.kefa.api.dto.company.request.CompanyDeleteRequest;
import com.kefa.api.dto.company.request.CompanyUpdateRequest;
import com.kefa.api.dto.company.response.CompanyAddResponse;
import com.kefa.api.dto.company.response.CompanyResponse;
import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.CompanyException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.NtsException;
import com.kefa.domain.entity.Account;
import com.kefa.domain.entity.Company;
import com.kefa.infrastructure.client.nts.dto.status.BusinessStatusResponse;
import com.kefa.infrastructure.client.nts.dto.status.BusinessStatusData;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.repository.CompanyRepository;
import com.kefa.infrastructure.security.auth.AuthenticationInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CompanyUseCaseTest {

    private static final Long COMPANY_ID = 1L;
    private static final Long COMPANY_ID_2 = 2L;
    private static final Long ACCOUNT_ID = 1L;
    private static final Long DIFFERENT_ACCOUNT_ID = 2L;
    private static final Long REVENUE = 1000L;
    private static final Long REVENUE_2 = 2000L;
    private static final Long UPDATED_REVENUE = 2000L;

    private static final String PASSWORD = "password123";
    private static final String WRONG_PASSWORD = "wrongPassword";
    private static final String ENCODED_PASSWORD = "encodedPassword123";
    private static final String COMPANY_NAME = "test";
    private static final String COMPANY_NAME_2 = "test2";
    private static final String UPDATED_COMPANY_NAME = "수정된이름";
    private static final String BUSINESS_NUMBER = "123-456-78901";
    private static final String BUSINESS_NUMBER_2 = "123-456-78902";
    private static final String ADDRESS = "주소테스트";
    private static final String ADDRESS_2 = "주소테스트2";
    private static final String UPDATED_ADDRESS = "수정된주소";
    private static final String INDUSTRY = "업종테스트";
    private static final String INDUSTRY_2 = "업종테스트2";
    private static final String UPDATED_INDUSTRY = "수정된업종";
    private static final String BUSINESS_STATUS_ACTIVE = "계속사업자";
    private static final String BUSINESS_STATUS_CLOSED = "폐업자";
    private static final String TAX_TYPE_NOT_REGISTERED = "국세청에 등록되지 않은 사업자등록번호입니다.";

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CompanyUseCase companyUseCase;

    private Account createAccount(Long accountId, String password) {
        return Account.builder()
            .id(accountId)
            .password(password)
            .build();
    }

    private Company createCompany(Long companyId, String name, String businessNumber,
                                  String address, String industry, Long revenue, Account account) {
        return Company.builder()
            .id(companyId)
            .name(name)
            .businessNumber(businessNumber)
            .address(address)
            .industry(industry)
            .revenueMillion(revenue)
            .account(account)
            .build();
    }

    private CompanyUpdateRequest createUpdateRequest(Long companyId) {
        return CompanyUpdateRequest.builder()
            .id(companyId)
            .name(UPDATED_COMPANY_NAME)
            .address(UPDATED_ADDRESS)
            .industry(UPDATED_INDUSTRY)
            .revenueMillion(UPDATED_REVENUE)
            .build();
    }

    private CompanyAddRequest createAddRequest() {
        return CompanyAddRequest.builder()
            .name(COMPANY_NAME)
            .businessNumber(BUSINESS_NUMBER)
            .address(ADDRESS)
            .industry(INDUSTRY)
            .revenueMillion(REVENUE)
            .build();
    }

    private AuthenticationInfo createAuthInfo(Long accountId) {
        return AuthenticationInfo.builder()
            .id(accountId)
            .build();
    }

    private CompanyDeleteRequest createDeleteRequest(String password) {
        return CompanyDeleteRequest.builder()
            .password(password)
            .build();
    }

    @Test
    @DisplayName("회사 삭제 성공")
    void deleteSuccess() {
        //given
        Account account = createAccount(ACCOUNT_ID, ENCODED_PASSWORD);
        Company company = createCompany(COMPANY_ID, COMPANY_NAME, BUSINESS_NUMBER,
            ADDRESS, INDUSTRY, REVENUE, account);
        AuthenticationInfo authInfo = createAuthInfo(ACCOUNT_ID);
        CompanyDeleteRequest request = createDeleteRequest(PASSWORD);

        when(companyRepository.findCompanyById(COMPANY_ID)).thenReturn(Optional.of(company));
        when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        //when
        companyUseCase.delete(COMPANY_ID, request, authInfo);

        //then
        verify(companyRepository).delete(company);
    }

    @Test
    @DisplayName("회사 삭제 실패 - 회사가 존재하지 않음")
    void deleteFailCompanyNotFound() {
        //given
        CompanyDeleteRequest request = createDeleteRequest(PASSWORD);
        AuthenticationInfo authInfo = createAuthInfo(ACCOUNT_ID);

        when(companyRepository.findCompanyById(COMPANY_ID)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> companyUseCase.delete(COMPANY_ID, request, authInfo))
            .isInstanceOf(CompanyException.class)
            .hasMessage(ErrorCode.COMPANY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회사 삭제 실패 - 권한 없음")
    void deleteFailUnauthorized() {
        //given
        Account account = createAccount(ACCOUNT_ID, ENCODED_PASSWORD);
        Company company = createCompany(COMPANY_ID, COMPANY_NAME, BUSINESS_NUMBER,
            ADDRESS, INDUSTRY, REVENUE, account);
        AuthenticationInfo authInfo = createAuthInfo(DIFFERENT_ACCOUNT_ID);
        CompanyDeleteRequest request = createDeleteRequest(PASSWORD);

        when(companyRepository.findCompanyById(COMPANY_ID)).thenReturn(Optional.of(company));

        //when & then
        assertThatThrownBy(() -> companyUseCase.delete(COMPANY_ID, request, authInfo))
            .isInstanceOf(CompanyException.class)
            .hasMessage(ErrorCode.NOT_COMPANY_OWNER.getMessage());
    }

    @Test
    @DisplayName("회사 삭제 실패 - 비밀번호 불일치")
    void deleteFailInvalidPassword() {
        //given
        Account account = createAccount(ACCOUNT_ID, ENCODED_PASSWORD);
        Company company = createCompany(COMPANY_ID, COMPANY_NAME, BUSINESS_NUMBER,
            ADDRESS, INDUSTRY, REVENUE, account);
        AuthenticationInfo authInfo = createAuthInfo(ACCOUNT_ID);
        CompanyDeleteRequest request = createDeleteRequest(WRONG_PASSWORD);

        when(companyRepository.findCompanyById(COMPANY_ID)).thenReturn(Optional.of(company));
        when(passwordEncoder.matches(WRONG_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        //when & then
        assertThatThrownBy(() -> companyUseCase.delete(COMPANY_ID, request, authInfo))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage(ErrorCode.INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("회사 정보 수정 성공")
    void updateSuccess() {
        //given
        Account account = createAccount(ACCOUNT_ID, ENCODED_PASSWORD);
        Company company = createCompany(COMPANY_ID, COMPANY_NAME, BUSINESS_NUMBER,
            ADDRESS, INDUSTRY, REVENUE, account);
        CompanyUpdateRequest request = createUpdateRequest(COMPANY_ID);
        AuthenticationInfo authInfo = createAuthInfo(ACCOUNT_ID);

        when(companyRepository.findCompanyById(COMPANY_ID)).thenReturn(Optional.of(company));

        //when
        CompanyResponse response = companyUseCase.update(request, authInfo);

        //then
        assertThat(response.getId()).isEqualTo(request.getId());
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getAddress()).isEqualTo(request.getAddress());
        assertThat(response.getIndustry()).isEqualTo(request.getIndustry());
        assertThat(response.getRevenueMillion()).isEqualTo(request.getRevenueMillion());
    }

    @Test
    @DisplayName("회사 정보 수정 실패 - 회사가 존재하지 않음")
    void updateFailCompanyNotFound() {
        //given
        CompanyUpdateRequest request = createUpdateRequest(COMPANY_ID);
        AuthenticationInfo authInfo = createAuthInfo(ACCOUNT_ID);

        when(companyRepository.findCompanyById(COMPANY_ID)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> companyUseCase.update(request, authInfo))
            .isInstanceOf(CompanyException.class)
            .hasMessage(ErrorCode.COMPANY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회사 정보 수정 실패 - 권한 없음")
    void updateFailUnauthorized() {
        //given
        Account account = createAccount(ACCOUNT_ID, ENCODED_PASSWORD);
        Company company = createCompany(COMPANY_ID, COMPANY_NAME, BUSINESS_NUMBER,
            ADDRESS, INDUSTRY, REVENUE, account);
        CompanyUpdateRequest request = createUpdateRequest(COMPANY_ID);
        AuthenticationInfo authInfo = createAuthInfo(DIFFERENT_ACCOUNT_ID);

        when(companyRepository.findCompanyById(COMPANY_ID)).thenReturn(Optional.of(company));

        //when & then
        assertThatThrownBy(() -> companyUseCase.update(request, authInfo))
            .isInstanceOf(CompanyException.class)
            .hasMessage(ErrorCode.NOT_COMPANY_OWNER.getMessage());
    }

    @Test
    @DisplayName("회사 단일 조회 성공")
    void getMyCompanySuccess() {
        //given
        Account account = createAccount(ACCOUNT_ID, ENCODED_PASSWORD);
        Company company = createCompany(COMPANY_ID, COMPANY_NAME, BUSINESS_NUMBER,
            ADDRESS, INDUSTRY, REVENUE, account);
        AuthenticationInfo authInfo = createAuthInfo(ACCOUNT_ID);

        when(companyRepository.findCompanyById(COMPANY_ID)).thenReturn(Optional.of(company));

        //when
        CompanyResponse response = companyUseCase.getMyCompany(COMPANY_ID, authInfo);

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
        AuthenticationInfo authInfo = createAuthInfo(ACCOUNT_ID);

        when(companyRepository.findCompanyById(COMPANY_ID)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> companyUseCase.getMyCompany(COMPANY_ID, authInfo))
            .isInstanceOf(CompanyException.class)
            .hasMessage(ErrorCode.COMPANY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("회사 단일 조회 실패 - 권한 없음")
    void getMyCompanyFailUnauthorized() {
        //given
        Account account = createAccount(ACCOUNT_ID, ENCODED_PASSWORD);
        Company company = createCompany(COMPANY_ID, COMPANY_NAME, BUSINESS_NUMBER,
            ADDRESS, INDUSTRY, REVENUE, account);
        AuthenticationInfo authInfo = createAuthInfo(DIFFERENT_ACCOUNT_ID);

        when(companyRepository.findCompanyById(COMPANY_ID)).thenReturn(Optional.of(company));

        //when & then
        assertThatThrownBy(() -> companyUseCase.getMyCompany(COMPANY_ID, authInfo))
            .isInstanceOf(CompanyException.class)
            .hasMessage(ErrorCode.NOT_COMPANY_OWNER.getMessage());
    }

    @Test
    @DisplayName("회사 목록 조회 성공")
    void findAllByAccountIdSuccess() {
        //given
        Account account = createAccount(ACCOUNT_ID, ENCODED_PASSWORD);
        List<Company> companies = List.of(
            createCompany(COMPANY_ID, COMPANY_NAME, BUSINESS_NUMBER,
                ADDRESS, INDUSTRY, REVENUE, account),
            createCompany(COMPANY_ID_2, COMPANY_NAME_2, BUSINESS_NUMBER_2,
                ADDRESS_2, INDUSTRY_2, REVENUE_2, account)
        );

        AuthenticationInfo authInfo = createAuthInfo(ACCOUNT_ID);

        when(companyRepository.findAllByAccountId(ACCOUNT_ID)).thenReturn(companies);

        //when
        List<CompanyResponse> responses = companyUseCase.getMyCompanies(authInfo);

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
    @DisplayName("회사 목록 조회 회사가 없을 경우 빈 리스트")
    void findAllByAccountIdEmpty() {
        //given
        AuthenticationInfo authInfo = createAuthInfo(ACCOUNT_ID);

        when(companyRepository.findAllByAccountId(ACCOUNT_ID)).thenReturn(Collections.emptyList());

        //when
        List<CompanyResponse> responses = companyUseCase.getMyCompanies(authInfo);

        //then
        assertNotNull(responses);
        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("회사 등록 성공")
    void addSuccess() {
        //given
        CompanyAddRequest request = createAddRequest();
        AuthenticationInfo authInfo = createAuthInfo(ACCOUNT_ID);
        Account account = createAccount(ACCOUNT_ID, ENCODED_PASSWORD);

        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        //when
        CompanyAddResponse response = companyUseCase.add(request, authInfo);

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
        CompanyAddRequest request = createAddRequest();
        AuthenticationInfo authInfo = createAuthInfo(ACCOUNT_ID);

        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> companyUseCase.add(request, authInfo))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage(ErrorCode.ACCOUNT_NOT_FOUND.getMessage());
    }



    @Test
    @DisplayName("사업자번호가 정상일 경우 성공")
    void validateBusinessNumberSuccess() {
        // given
        BusinessStatusData data = BusinessStatusData.builder()
            .businessStatus(BUSINESS_STATUS_ACTIVE)
            .build();

        BusinessStatusResponse response = BusinessStatusResponse.builder()
            .data(List.of(data))
            .build();

        // when & then
        assertDoesNotThrow(() -> companyUseCase.validateBusinessNumber(response));
    }

    @Test
    @DisplayName("사업자번호가 존재하지 않을 경우 예외")
    void validateBusinessNumberEmptyData() {
        // given
        BusinessStatusResponse response = BusinessStatusResponse.builder()
            .data(Collections.emptyList())
            .build();

        // when & then
        NtsException exception = assertThrows(NtsException.class,
            () -> companyUseCase.validateBusinessNumber(response));
        assertEquals(ErrorCode.BUSINESS_NUMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("사업자번호가 미등록 상태일 경우 예외")
    void validateBusinessNumberNotRegistered() {
        // given
        BusinessStatusData data = BusinessStatusData.builder()
            .taxType(TAX_TYPE_NOT_REGISTERED)
            .build();

        BusinessStatusResponse response = BusinessStatusResponse.builder()
            .data(List.of(data))
            .build();

        // when & then
        NtsException exception = assertThrows(NtsException.class,
            () -> companyUseCase.validateBusinessNumber(response));
        assertEquals(ErrorCode.BUSINESS_NUMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("사업자번호가 휴/폐업 상태일 경우 예외")
    void validateBusinessNumberInactive() {
        // given
        BusinessStatusData data = BusinessStatusData.builder()
            .businessStatus(BUSINESS_STATUS_CLOSED)
            .build();

        BusinessStatusResponse response = BusinessStatusResponse.builder()
            .data(List.of(data))
            .build();

        // when & then
        NtsException exception = assertThrows(NtsException.class,
            () -> companyUseCase.validateBusinessNumber(response));
        assertEquals(ErrorCode.INACTIVE_BUSINESS_NUMBER, exception.getErrorCode());
    }

}
