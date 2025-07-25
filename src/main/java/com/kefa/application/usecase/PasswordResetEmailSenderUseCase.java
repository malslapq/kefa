package com.kefa.application.usecase;

import com.kefa.infrastructure.mail.EmailSender;
import com.kefa.infrastructure.repository.PasswordResetCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetEmailSenderUseCase {

    private final EmailSender emailSender;
    private final PasswordResetCacheRepository passwordResetCacheRepository;

    /**
     * Initiates the password reset process by generating a unique token, storing it with the provided email, and sending a password reset email to the user.
     *
     * @param email the email address to which the password reset instructions will be sent
     */
    @Async
    public void sendPasswordResetEmail(String email) {
        String token = UUID.randomUUID().toString();
        passwordResetCacheRepository.save(token, email);
        emailSender.sendPasswordResetEmail(email, token);
    }

}
