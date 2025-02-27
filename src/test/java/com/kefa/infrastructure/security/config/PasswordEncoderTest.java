package com.kefa.infrastructure.security.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordEncoderTest {

    private PasswordEncoder passwordEncoder;
    private String password;
    private String encodedPassword;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        password = "passwordTest";
        encodedPassword = passwordEncoder.encode(password);
    }

    @Test
    @DisplayName("기존 비밀번호와 암호화된 비밀번호 다름")
    void passwordDifferentTest() {
        assertThat(encodedPassword).isNotEqualTo(password);
    }

    @Test
    @DisplayName("암호화된 비밀번호 BCrypt 형식 확인")
    void passwordFormatTest() {
        assertThat(encodedPassword).startsWith("$2a$");
    }

    @Test
    @DisplayName("비밀번호와 암호화된 비밀번호 검증 성공")
    void passwordMatchSuccessTest() {
        assertThat(passwordEncoder.matches(password, encodedPassword)).isTrue();
    }

    @Test
    @DisplayName("비밀번호와 암호화된 비밀번호 검증 실패")
    void passwordMatchFailTest() {
        assertThat(passwordEncoder.matches("wrongPassword", encodedPassword)).isFalse();
    }
}