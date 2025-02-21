package com.kefa.infrastructure.security.jwt;

import com.kefa.domain.type.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyByte = Decoders.BASE64.decode(jwtProperties.getKey());
        this.key = Keys.hmacShaKeyFor(keyByte);
    }

    private String createToken(Long id, Role role, long expirationTime) {
        Date nowDate = new Date();
        Date expirationDate = new Date(nowDate.getTime() + expirationTime);

        return Jwts.builder()
            .subject(String.valueOf(id))
            .claim("role", role.name())
            .issuedAt(nowDate)
            .expiration(expirationDate)
            .signWith(key)
            .compact();
    }

    public String createAccessToken(Long id, Role role) {
        return createToken(id, role, jwtProperties.getAccessExpirationTime());
    }

    public String createRefreshToken(Long id, Role role) {
        return createToken(id, role, jwtProperties.getRefreshExpirationTime());
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
            throw e;
        } catch (MalformedJwtException e) {
            log.info("잘못된 JWT 토큰입니다.: {}", e.getMessage());
            throw e;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 비어있습니다.: {}", e.getMessage());
            throw e;
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
        String subject = getClaims(token).getSubject();
        return Long.valueOf(subject);
    }

    public Role getRole(String token) {
        return Role.valueOf(getClaims(token).get("role", String.class));
    }
}
