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

    @Async
    public void sendPasswordResetEmail(String email) {
        String token = UUID.randomUUID().toString();
        passwordResetCacheRepository.save(token, email);
        emailSender.sendPasswordResetEmail(email, token);
    }

}
