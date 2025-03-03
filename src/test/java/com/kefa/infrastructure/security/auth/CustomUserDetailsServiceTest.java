package com.kefa.infrastructure.security.auth;

import com.kefa.domain.entity.Account;
import com.kefa.domain.type.LoginType;
import com.kefa.domain.type.Role;
import com.kefa.domain.type.SubscriptionType;
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
import java.util.Set;

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
            .loginTypes(Set.of(LoginType.LOCAL))
            .subscriptionType(SubscriptionType.FREE)
            .role(Role.ACCOUNT)
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
            .contains("ROLE_" + Role.ACCOUNT.name());
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
