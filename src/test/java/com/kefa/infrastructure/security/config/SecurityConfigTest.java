package com.kefa.infrastructure.security.config;

import com.kefa.application.usecase.AuthenticationUseCase;
import com.kefa.infrastructure.security.auth.OAuth2SuccessHandler;
import com.kefa.infrastructure.security.jwt.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SecurityConfig.class)
@AutoConfigureMockMvc
@MockBean(JpaMetamodelMappingContext.class)
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private OAuth2SuccessHandler oAuth2SuccessHandler;

    @MockBean
    private AuthenticationUseCase authenticationUseCase;

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