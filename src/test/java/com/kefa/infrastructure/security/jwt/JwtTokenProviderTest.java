package com.kefa.infrastructure.security.jwt;

import com.kefa.domain.type.Role;
import com.kefa.infrastructure.security.auth.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class JwtTokenProviderTest {

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private JwtTokenProvider jwtTokenProvider;
    private JwtTokenProvider expiredTokenProvider;
    private String expiredToken;

    @BeforeEach
    void setUp() throws InterruptedException {
        // 일반 토큰 Provider
        JwtProperties properties = new JwtProperties();
        ReflectionTestUtils.setField(properties, "key", "testSecretKeytestSecretKeytestSecretKeytestSecretKey");
        ReflectionTestUtils.setField(properties, "accessExpirationTime", 3600L);
        ReflectionTestUtils.setField(properties, "refreshExpirationTime", 72000L);
        jwtTokenProvider = new JwtTokenProvider(properties);
        jwtTokenProvider.init();

        // 만료 토큰 Provider
        JwtProperties expiredProperties = new JwtProperties();
        ReflectionTestUtils.setField(expiredProperties, "key", "testSecretKeytestSecretKeytestSecretKeytestSecretKey");
        ReflectionTestUtils.setField(expiredProperties, "accessExpirationTime", 1L); // 1ms
        ReflectionTestUtils.setField(expiredProperties, "refreshExpirationTime", 1L);
        expiredTokenProvider = new JwtTokenProvider(expiredProperties);
        expiredTokenProvider.init();

        // 만료된 토큰 생성
        expiredToken = expiredTokenProvider.createAccessToken(1L, Role.ACCOUNT);
        Thread.sleep(10); // 토큰 만료를 위해 대기
    }

    @Test
    @DisplayName("액세스 토큰 생성 성공 테스트")
    void createAccessTokenSuccess() {
        // given
        Long id = 1L;
        Role role = Role.ACCOUNT;

        // when
        String token = jwtTokenProvider.createAccessToken(id, role);

        // then
        assertThat(token).isNotNull();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getId(token)).isEqualTo(id);
        assertThat(jwtTokenProvider.getRole(token)).isEqualTo(role);
    }

    @Test
    @DisplayName("리프레시 토큰 생성 성공 테스트")
    void createRefreshTokenSuccess() {
        // given
        Long id = 1L;
        Role role = Role.ACCOUNT;

        // when
        String token = jwtTokenProvider.createRefreshToken(id, role);

        // then
        assertThat(token).isNotNull();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getId(token)).isEqualTo(id);
        assertThat(jwtTokenProvider.getRole(token)).isEqualTo(role);
    }

    @Test
    @DisplayName("관리자 권한으로 토큰 생성 성공 테스트")
    void createTokenWithAdminRoleSuccess() {
        // given
        Long id = 1L;
        Role role = Role.ADMIN;

        // when
        String token = jwtTokenProvider.createAccessToken(id, role);

        // then
        assertThat(jwtTokenProvider.getRole(token)).isEqualTo(Role.ADMIN);
        assertThat(jwtTokenProvider.getRole(token).getRole()).isEqualTo("관리자");
    }

    @Test
    @DisplayName("전문가 권한으로 토큰 생성 성공 테스트")
    void createTokenWithExpertRoleSuccess() {
        // given
        Long id = 1L;
        Role role = Role.EXPERT;

        // when
        String token = jwtTokenProvider.createAccessToken(id, role);

        // then
        assertThat(jwtTokenProvider.getRole(token)).isEqualTo(Role.EXPERT);
        assertThat(jwtTokenProvider.getRole(token).getRole()).isEqualTo("전문가");
    }

    @Test
    @DisplayName("직원 권한으로 토큰 생성 성공 테스트")
    void createTokenWithStaffRoleSuccess() {
        // given
        Long id = 1L;
        Role role = Role.STAFF;

        // when
        String token = jwtTokenProvider.createAccessToken(id, role);

        // then
        assertThat(jwtTokenProvider.getRole(token)).isEqualTo(Role.STAFF);
        assertThat(jwtTokenProvider.getRole(token).getRole()).isEqualTo("직원");
    }

    @Test
    @DisplayName("유효한 토큰 검증 성공 테스트")
    void validateTokenSuccess() {
        // given
        String token = jwtTokenProvider.createAccessToken(1L, Role.ACCOUNT);

        // when
        boolean isValid = jwtTokenProvider.validateToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("잘못된 토큰 검증 실패 테스트")
    void validateTokenFailWithInvalidToken() {
        // given
        String testToken = "this is token";

        // when & then
        assertThrows(MalformedJwtException.class, () ->
            jwtTokenProvider.validateToken(testToken)
        );
    }

    @Test
    @DisplayName("토큰에서 ID 추출 성공 테스트")
    void getIdFromTokenSuccess() {
        // given
        Long id = 1L;
        String token = jwtTokenProvider.createAccessToken(id, Role.ACCOUNT);

        // when
        Long getId = jwtTokenProvider.getId(token);

        // then
        assertThat(getId).isEqualTo(id);
    }

    @Test
    @DisplayName("토큰에서 Role 추출 성공 테스트")
    void getRoleSuccess() {
        // given
        Role role = Role.ACCOUNT;
        String token = jwtTokenProvider.createAccessToken(1L, role);

        // when
        Role getRole = jwtTokenProvider.getRole(token);

        // then
        assertThat(getRole).isEqualTo(role);
        assertThat(getRole.getRole()).isEqualTo("회원");
    }

    @Test
    @DisplayName("만료된 토큰 검증 실패 테스트")
    void validateTokenFailWithExpiredToken() {

        // when & then
        assertThrows(ExpiredJwtException.class, () ->
            jwtTokenProvider.validateToken(expiredToken)
        );
    }

    @Test
    @DisplayName("만료된 토큰에서 정보 추출 시 예외 발생 테스트")
    void getInfoFromExpiredTokenThrowsException() {

        // when & then
        assertThrows(ExpiredJwtException.class, () -> jwtTokenProvider.getId(expiredToken));
        assertThrows(ExpiredJwtException.class, () -> jwtTokenProvider.getRole(expiredToken));
    }

}
