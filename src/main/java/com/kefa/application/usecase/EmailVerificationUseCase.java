package com.kefa.application.usecase;

import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.EmailException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Account;
import com.kefa.infrastructure.mail.EmailSender;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.repository.EmailVerificationInMemoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationUseCase {

    private final EmailVerificationInMemoryRepository emailVerificationInMemoryRepository;
    private final EmailSender emailSender;
    private final AccountRepository accountRepository;

    @Async
    @Retryable(
        retryFor = EmailException.class,
        backoff = @Backoff(delay = 5000)
    )
    public void sendVerificationEmail(String email) {

        String token = UUID.randomUUID().toString();
        emailVerificationInMemoryRepository.saveEmailToken(token, email);
        emailSender.sendVerificationEmail(email, token);

    }

    @Recover
    public void recoverFromSendVerificationEmailFailed(EmailException exception, String email) {
        emailVerificationInMemoryRepository.deleteByEmail(email);
        log.error("인증 메일 발송 재시도 실패: {}" , exception.getMessage());
    }

    @Transactional
    public void verify(String token) {

        String email = emailVerificationInMemoryRepository.findByEmailToken(token);

        if (email == null) {
            throw new AuthenticationException(ErrorCode.INVALID_EMAIL_VERIFICATION_TOKEN);
        }

        Account account = accountRepository.findByEmail(email)
            .orElseThrow(() -> new AuthenticationException(ErrorCode.NOT_FOUND_ACCOUNT));

        account.verify();
        emailVerificationInMemoryRepository.deleteByEmailToken(token);

    }

    public void resendEmail(String email) {

        Account account = accountRepository.findByEmail(email)
            .orElseThrow(() -> new AuthenticationException(ErrorCode.NOT_FOUND_ACCOUNT));

        if (account.isEmailVerified()) {
            throw new AuthenticationException(ErrorCode.ALREADY_VERIFIED_EMAIL);
        }

        sendVerificationEmail(email);

    }


}
