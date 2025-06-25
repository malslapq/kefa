package com.kefa.infrastructure.security.jwt;

import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.JwtAuthenticationException;
import com.kefa.common.type.Role;
import com.kefa.infrastructure.security.cipher.CipherService;
import com.kefa.infrastructure.security.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private static final String CLAIM_ROLE_SUBJECT = "role";
    private static final String CLAIM_NAME_SUBJECT = "name";
    private static final String CLAIM_JWT_ID_SUBJECT = "jwtId";

    private final JwtProperties jwtProperties;
    private final CipherService cipherService;
    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyByte = Decoders.BASE64.decode(jwtProperties.getKey());
        this.key = Keys.hmacShaKeyFor(keyByte);
    }

    private String createToken(Long id, Role role, String name, long expirationTime, boolean includeJwtId) {

        Date nowDate = new Date();
        Date expirationDate = new Date(nowDate.getTime() + expirationTime);

        String encryptedId = cipherService.encrypt(String.valueOf(id));
        String encryptedRole = cipherService.encrypt(String.valueOf(role));
        String encryptedName = cipherService.encrypt(name);


        JwtBuilder jwtBuilder = Jwts.builder()
            .subject(encryptedId)
            .claim(CLAIM_ROLE_SUBJECT, encryptedRole)
            .claim(CLAIM_NAME_SUBJECT, encryptedName)

            .issuedAt(nowDate)
            .expiration(expirationDate)
            .signWith(key);

        if(includeJwtId) {
            String encryptedJwtId = cipherService.encrypt(UUID.randomUUID().toString());
            jwtBuilder.claim(CLAIM_JWT_ID_SUBJECT, encryptedJwtId);
        }

        return jwtBuilder.compact();

    }

    public String createAccessToken(Long id, Role role, String name) {
        return createToken(id, role, name, jwtProperties.getAccessExpirationTime(), true);
    }

    public String createRefreshToken(Long id, Role role, String name) {
        return createToken(id, role, name, jwtProperties.getRefreshExpirationTime(), false);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (SecurityException | io.jsonwebtoken.security.SecurityException e) {
            log.info("유효하지 않은 JWT 서명입니다.: {}", e.getMessage());
            throw new JwtAuthenticationException(ErrorCode.INVALID_JWT_SIGNATURE);
        } catch (MalformedJwtException e) {
            log.info("잘못된 JWT 토큰입니다.: {}", e.getMessage());
            throw new JwtAuthenticationException(ErrorCode.INVALID_JWT_TOKEN);
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.: {}", e.getMessage());
            throw new JwtAuthenticationException(ErrorCode.EXPIRED_JWT_TOKEN);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.: {}", e.getMessage());
            throw new JwtAuthenticationException(ErrorCode.UNSUPPORTED_JWT_TOKEN);
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 비어있습니다.: {}", e.getMessage());
            throw new JwtAuthenticationException(ErrorCode.EMPTY_JWT_TOKEN);
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public Long getId(String token) {
        String encryptedSubject = getClaims(token).getSubject();
        return Long.valueOf(cipherService.decrypt(encryptedSubject));
    }

    public Role getRole(String token) {
        String encryptedRole = getClaims(token).get(CLAIM_ROLE_SUBJECT, String.class);
        return Role.valueOf(cipherService.decrypt(encryptedRole));
    }

    public String getName(String token){
        String encryptedName = getClaims(token).get(CLAIM_NAME_SUBJECT, String.class);
        return cipherService.decrypt(encryptedName);
    }

    public String getJwtId(String token){
        String encryptedJwtId = getClaims(token).get(CLAIM_JWT_ID_SUBJECT, String.class);
        return cipherService.decrypt(encryptedJwtId);
    }

    public LocalDateTime getTokenExpiration(String token) {

        Date expirationDate = getClaims(token).getExpiration();

        return LocalDateTime.ofInstant(
            expirationDate.toInstant(),
            ZoneId.systemDefault()
        );
    }

}
