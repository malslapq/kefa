package com.kefa.infrastructure.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@RequiredArgsConstructor
public class CipherConfig {

    private final CipherProperties cipherProperties;

    public Cipher createCipher(int mode) throws Exception {

        SecretKeySpec secretKey = new SecretKeySpec(
            cipherProperties.getSecretKey().getBytes(),
            "AES"
        );
        IvParameterSpec iv = new IvParameterSpec(
            cipherProperties.getIv().getBytes()
        );

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(mode, secretKey, iv);

        return cipher;

    }
}
