package com.kefa.application.usecase;

import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Account;
import com.kefa.infrastructure.mail.EmailSender;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.repository.EmailVerificationInMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationUseCase {

    private final EmailVerificationInMemoryRepository emailVerificationInMemoryRepository;
    private final EmailSender emailSender;
    private final AccountRepository accountRepository;

    public void sendVerificationEmail(String email) {

        String token = UUID.randomUUID().toString();
        emailVerificationInMemoryRepository.saveEmailToken(token, email);
        emailSender.sendVerificationEmail(email, token);

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
