package com.kefa.application.usecase;


import com.kefa.api.dto.account.request.AccountUpdateSubscriptionTypeRequest;
import com.kefa.api.dto.account.command.AccountsGetCommand;
import com.kefa.api.dto.account.request.AccountUpdatePasswordRequestFromAdmin;
import com.kefa.api.dto.account.request.AccountUpdateRequest;
import com.kefa.api.dto.account.request.AccountUpdateRoleRequest;
import com.kefa.api.dto.account.response.AccountDetailResponse;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.common.exception.AdminException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.type.LoginType;
import com.kefa.common.type.Role;
import com.kefa.common.type.SubscriptionType;
import com.kefa.domain.entity.Account;
import com.kefa.domain.entity.SocialInfo;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.repository.SocialInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
class AdminUseCaseTest {

    @InjectMocks
    private AdminUseCase adminUseCase;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private SocialInfoRepository socialInfoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Account account;
    private final Long accountId = 1L;

    @BeforeEach
    void setUp() {
        account = Account.builder()
            .id(accountId)
            .email("test@test.com")
            .name("tester")
            .password("encodedPassword")
            .role(Role.ADMIN)
            .subscriptionType(SubscriptionType.FREE)
            .emailVerified(false)
            .socialInfos(new ArrayList<>())
            .build();
    }

    @DisplayName("소셜 정보 삭제 성공")
    @Test
    void deleteAccountSocialInfoSuccess() {
        SocialInfo social = SocialInfo.builder()
            .loginType(LoginType.GOOGLE)
            .providerUserId("test")
            .build();
        account.getSocialInfos().add(social);

        given(accountRepository.findByIdWithSocialInfos(accountId)).willReturn(Optional.of(account));

        adminUseCase.deleteAccountSocialInfo(accountId);

        assertThat(account.getSocialInfos()).isEmpty();
        verify(socialInfoRepository, times(1)).deleteAll(anyCollection());
    }

    @DisplayName("소셜 정보 삭제 실패 - 계정 없음")
    @Test
    void deleteAccountSocialInfoFail() {
        given(accountRepository.findByIdWithSocialInfos(accountId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminUseCase.deleteAccountSocialInfo(accountId))
            .isInstanceOf(AdminException.class)
            .hasMessage(ErrorCode.NOT_FOUND_ACCOUNT.getMessage());
    }

    @DisplayName("계정 삭제 성공")
    @Test
    void deleteAccountSuccess() {
        adminUseCase.deleteAccount(accountId);
        verify(accountRepository, times(1)).deleteById(accountId);
    }

    @DisplayName("구독 타입 수정 성공")
    @Test
    void updateAccountSubscriptionTypeSuccess() {
        AccountUpdateSubscriptionTypeRequest request = new AccountUpdateSubscriptionTypeRequest(SubscriptionType.PREMIUM);

        given(accountRepository.findById(accountId)).willReturn(Optional.of(account));

        AccountDetailResponse response = adminUseCase.updateAccountSubscriptionType(accountId, request);

        assertThat(response.getSubscriptionType()).isEqualTo(SubscriptionType.PREMIUM);
    }

    @DisplayName("권한 수정 성공")
    @Test
    void updateAccountRoleSuccess() {
        AccountUpdateRoleRequest request = new AccountUpdateRoleRequest(com.kefa.common.type.Role.ADMIN);

        given(accountRepository.findById(accountId)).willReturn(Optional.of(account));

        AccountDetailResponse response = adminUseCase.updateAccountRole(accountId, request);

        assertThat(response.getRole()).isEqualTo(com.kefa.common.type.Role.ADMIN);
    }

    @DisplayName("비밀번호 수정 성공")
    @Test
    void updateAccountPasswordSuccess() {
        String newPassword = "newPassword123!";
        String encodedPassword = "encodedNewPassword";

        AccountUpdatePasswordRequestFromAdmin request = new AccountUpdatePasswordRequestFromAdmin(newPassword);

        given(accountRepository.findById(accountId)).willReturn(Optional.of(account));
        given(passwordEncoder.encode(newPassword)).willReturn(encodedPassword);

        AccountDetailResponse response = adminUseCase.updateAccountPassword(accountId, request);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(account.getEmail());
    }

    @DisplayName("계정 정보 수정 성공")
    @Test
    void updateAccountSuccess() {
        AccountUpdateRequest request = AccountUpdateRequest.builder()
            .email("new@test.com")
            .name("newName")
            .role(Role.ADMIN)
            .subscriptionType(SubscriptionType.PREMIUM)
            .emailVerified(true)
            .build();

        given(accountRepository.findById(accountId)).willReturn(Optional.of(account));

        AccountDetailResponse response = adminUseCase.updateAccount(accountId, request);

        assertThat(response.getEmail()).isEqualTo("new@test.com");
        assertThat(response.getRole()).isEqualTo(com.kefa.common.type.Role.ADMIN);
        assertThat(response.getSubscriptionType()).isEqualTo(SubscriptionType.PREMIUM);
    }

    @DisplayName("계정 목록 조회 성공")
    @Test
    void getAccountsSuccess() {
        Account another = Account.builder()
            .id(2L)
            .email("second@test.com")
            .name("second")
            .role(Role.FREE_ACCOUNT)
            .subscriptionType(SubscriptionType.FREE)
            .build();

        List<Account> accountList = List.of(account, another);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Account> page = new PageImpl<>(accountList, pageable, accountList.size());

        AccountsGetCommand command = AccountsGetCommand.builder()
            .keyword("test")
            .searchType("email")
            .page(0)
            .size(10)
            .build();

        given(accountRepository.findByEmailContaining(eq("test"), eq(pageable)))
            .willReturn(page);

        PagedResponse<AccountDetailResponse> response = adminUseCase.getAccounts(command);

        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getTotalElements()).isEqualTo(2);
    }
}
