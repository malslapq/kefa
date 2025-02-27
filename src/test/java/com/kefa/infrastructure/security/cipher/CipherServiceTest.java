package com.kefa.infrastructure.security.cipher;

import com.kefa.infrastructure.security.config.CipherConfig;
import com.kefa.infrastructure.security.config.CipherProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CipherServiceTest {

    private CipherService cipherService;

    @BeforeEach
    void setUp() {
        CipherProperties cipherProperties = new CipherProperties();
        cipherProperties.setSecretKey("1234567890123456");
        cipherProperties.setIv("abcdefghijklmnop");

        CipherConfig cipherConfig = new CipherConfig(cipherProperties);
        cipherService = new CipherService(cipherConfig);
    }

    @Test
    @DisplayName("문자열 암호화 성공 테스트")
    void encryptSuccess() {
        // given
        String originalText = "text";

        // when
        String encryptedText = cipherService.encrypt(originalText);

        // then
        assertThat(encryptedText).isNotEqualTo(originalText);
        assertThat(encryptedText).isNotBlank();
    }

    @Test
    @DisplayName("문자열 복호화 성공 테스트")
    void decryptSuccess() {
        // given
        String originalText = "text";
        String encryptedText = cipherService.encrypt(originalText);

        // when
        String decryptedText = cipherService.decrypt(encryptedText);

        // then
        assertThat(decryptedText).isEqualTo(originalText);
    }
}
