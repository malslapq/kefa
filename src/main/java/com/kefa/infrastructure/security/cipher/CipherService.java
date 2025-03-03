package com.kefa.infrastructure.security.cipher;

import com.kefa.common.exception.CipherException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.infrastructure.security.config.CipherConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CipherService {

    private final CipherConfig cipherConfig;

    public String encrypt(String value) {

        try {
            Cipher cipher = cipherConfig.createCipher(Cipher.ENCRYPT_MODE);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new CipherException(ErrorCode.ENCRYPTION_FAILED);
        }

    }

    public String decrypt(String encryptedValue) {

        try {
            Cipher cipher = cipherConfig.createCipher(Cipher.DECRYPT_MODE);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
            return new String(decrypted);
        } catch (Exception e) {
            throw new CipherException(ErrorCode.DECRYPTION_FAILED);
        }

    }

}
