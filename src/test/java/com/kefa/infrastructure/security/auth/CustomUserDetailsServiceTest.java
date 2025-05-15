package com.kefa.infrastructure.security.auth;

import com.kefa.domain.entity.Account;
import com.kefa.common.type.Role;
import com.kefa.common.type.SubscriptionType;
import com.kefa.infrastructure.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private AccountRepository accountRepository;

    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.builder()
            .id(1L)
            .email("test@example.com")
            .password("password")
            .subscriptionType(SubscriptionType.FREE)
            .role(Role.FREE_ACCOUNT)
            .build();
    }

    @Test
    @DisplayName("사용자 ID로 식별자 확인")
    void checkUsername() {
        // given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("1");

        // then
        assertThat(userDetails.getUsername()).isEqualTo("1");
    }

    @Test
    @DisplayName("사용자 비밀번호 확인")
    void checkPassword() {
        // given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("1");

        // then
        assertThat(userDetails.getPassword()).isEqualTo("password");
    }

    @Test
    @DisplayName("사용자 권한 확인")
    void checkAuthorities() {
        // given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("1");

        // then
        assertThat(userDetails.getAuthorities())
            .hasSize(1)
            .extracting("authority")
            .contains("ROLE_" + Role.FREE_ACCOUNT.name());
    }

    @Test
    @DisplayName("존재하지 않는 사용자 예외 테스트")
    void loadUserByUsername_NotFound() {
        // given
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("1"))
            .isInstanceOf(UsernameNotFoundException.class);
    }
}
