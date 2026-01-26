package org.example.auth.notification.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

//토큰을 암호화 하기 위한 컴포넌트
@Component
public class TokenCipher {

    //표준 권장길이
    private static final int IV_LEN = 12;
    private static final int TAG_LEN_BITS = 128;

    //포맷 버전
    private static final byte FORMAT_V1 = 0x01;

    private final SecureRandom random = new SecureRandom();
    private final SecretKey key;

    public TokenCipher(@Value("${security.kakao.refresh-token-enc-key-b64}") String keyB64) {
        byte[] keyBytes = Base64.getDecoder().decode(keyB64);

        // 키길이 검증
        if (keyBytes.length != 32) {
            throw new IllegalStateException(
                    "Invalid AES key length. Expected 32 bytes (AES-256) after base64 decoding, but got: " + keyBytes.length
            );
        }

        //  대칭키 생성
        this.key = new SecretKeySpec(keyBytes, "AES");
    }

    //평문-> 암호화
    public String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isBlank()) {
            throw new IllegalArgumentException("plaintext must not be null/blank");
        }

        try {
            // IV 생성 매 암호화마다 랜덤생성
            byte[] iv = new byte[IV_LEN];
            random.nextBytes(iv);

            // Cipher 초기화
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LEN_BITS, iv));

            //암호화 수행
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // 포맷
            ByteBuffer buf = ByteBuffer.allocate(1 + iv.length + ciphertext.length);
            buf.put(FORMAT_V1);
            buf.put(iv);
            buf.put(ciphertext);

            return Base64.getEncoder().encodeToString(buf.array());

        } catch (Exception e) {
            throw new IllegalStateException("refresh token encrypt failed", e);
        }
    }

    //암호문 -> 평문
    public String decrypt(String encBase64) {
        if (encBase64 == null || encBase64.isBlank()) {
            throw new IllegalArgumentException("encBase64 must not be null/blank");
        }

        try {
            byte[] all = Base64.getDecoder().decode(encBase64);
            ByteBuffer buf = ByteBuffer.wrap(all);

            // 버전 읽기
            byte version = buf.get();
            if (version != FORMAT_V1) {
                throw new IllegalStateException("Unsupported token cipher format version: " + version);
            }

            // IV 읽기
            byte[] iv = new byte[IV_LEN];
            buf.get(iv);

            // ciphertext+tag 읽기
            byte[] ciphertext = new byte[buf.remaining()];
            buf.get(ciphertext);

            // 복호화 수행
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LEN_BITS, iv));
            byte[] plaintext = cipher.doFinal(ciphertext);

            return new String(plaintext, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new IllegalStateException("refresh token decrypt failed", e);
        }
    }
}
