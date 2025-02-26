package com.kefa.application.usecase;

import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Account;
import com.kefa.infrastructure.mail.EmailSender;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.repository.EmailVerificationRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationUseCase {

    private final EmailVerificationRedisRepository emailVerificationRedisRepository;
    private final EmailSender emailSender;
    private final AccountRepository accountRepository;

    public void sendVerificationEmail(String email) {
        String token = UUID.randomUUID().toString();
        emailVerificationRedisRepository.saveEmailToken(token, email);
        emailSender.sendVerificationEmail(email, token);
    }

    @Transactional
    public void verifyEmail(String token) {
        String email = emailVerificationRedisRepository.getEmailByToken(token);
        if (email == null) {
            throw new AuthenticationException(ErrorCode.INVALID_EMAIL_VERIFICATION_TOKEN);
        }

        Account account = accountRepository.findByEmail(email)
            .orElseThrow(() -> new AuthenticationException(ErrorCode.ACCOUNT_NOT_FOUND));

        account.verify();
        emailVerificationRedisRepository.removeEmailToken(token);
    }
}
