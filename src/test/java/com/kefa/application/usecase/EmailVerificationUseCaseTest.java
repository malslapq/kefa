package com.kefa.application.usecase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.kefa.infrastructure.mail.EmailSender;
import com.kefa.infrastructure.repository.EmailVerificationRedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EmailVerificationUseCaseTest {

    @Mock
    private EmailVerificationRedisRepository emailVerificationRedisRepository;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private EmailVerificationUseCase emailVerificationUseCase;

    @Test
    @DisplayName("이메일 인증 메일 발송 성공")
    void sendVerificationEmail_success() {
        // given
        String email = "test@email.com";

        doNothing().when(emailVerificationRedisRepository).saveEmailToken(any(), eq(email));
        doNothing().when(emailSender).sendVerificationEmail(eq(email), any());

        // when
        emailVerificationUseCase.sendVerificationEmail(email);

        // then
        verify(emailVerificationRedisRepository).saveEmailToken(any(), eq(email));
        verify(emailSender).sendVerificationEmail(eq(email), any());
    }

    @Test
    @DisplayName("Redis 저장 실패시 이메일 발송하지 않음")
    void sendVerificationEmail_failOnRedis() {
        // given
        String email = "test@email.com";
        doThrow(new RuntimeException()).when(emailVerificationRedisRepository).saveEmailToken(any(), eq(email));

        // when & then
        assertThatThrownBy(() -> emailVerificationUseCase.sendVerificationEmail(email))
            .isInstanceOf(RuntimeException.class);

        verify(emailVerificationRedisRepository).saveEmailToken(any(), eq(email));
        verify(emailSender, never()).sendVerificationEmail(any(), any());
    }
}