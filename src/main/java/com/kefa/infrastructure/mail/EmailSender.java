package com.kefa.infrastructure.mail;

import com.kefa.common.exception.EmailException;
import com.kefa.common.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender javaMailSender;

    public void sendVerificationEmail(String to, String token) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Kefa 이메일 인증");
            helper.setText(EmailTemplate.createVerificationEmailContent(token), true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailException(ErrorCode.SERVER_ERROR);
        }

    }

}
