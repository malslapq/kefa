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

    /**
     * Sends a password reset email with a reset link to the specified email address.
     *
     * @param email the recipient's email address
     * @param token the password reset token to include in the email content
     * @throws EmailException if sending the email fails due to a messaging error
     */
    public void sendPasswordResetEmail(String email, String token) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Kefa 비밀번호 재설정 링크");
            helper.setText(EmailTemplate.createPasswordResetEmailContent(token), true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailException(ErrorCode.SERVER_ERROR);
        }

    }

    /**
     * Sends a verification email with a unique token to the specified recipient.
     *
     * The email contains an HTML-formatted verification link generated using the provided token.
     *
     * @param to the recipient's email address
     * @param token the unique verification token to include in the email content
     * @throws EmailException if the email fails to send due to a messaging error
     */
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
