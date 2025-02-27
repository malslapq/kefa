package com.kefa.infrastructure.security.config;

import com.kefa.infrastructure.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("공개 엔드포인트 접근 가능 성공")
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
}
