package com.kefa.infrastructure.security.config;

import com.kefa.infrastructure.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private MockMvc mockMvc;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final String password = "passwordTest";
    private final String encodedPassword = passwordEncoder.encode(password);

    @Test
    @DisplayName("공개 엔드포인드 접근 가능 성공")
    void publicEndpointsSuccessTest() throws Exception {

        List<String> publicUrls = Arrays.asList(
            "/auth/login",
            "/auth/join",
            "/public/index"
        );

        for (String url : publicUrls) {
            mockMvc.perform(get(url))
                .andExpect(status().isNotFound());
        }

    }

    @Test
    @DisplayName("보호된 엔드포인트 접근 실패")
    void protectedEndpointsFailTest() throws Exception {

        List<String> protectedUrls = Arrays.asList(
            "/member/profile",
            "/admin/profile",
            "/consulting/document/1"
        );

        for (String url : protectedUrls) {
            mockMvc.perform(get(url))
                .andExpect(status().isUnauthorized())
                .andDo(print());
        }

    }

    @Test
    @DisplayName("기존 비밀번호화 암호화된 비밀번호 다름")
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
