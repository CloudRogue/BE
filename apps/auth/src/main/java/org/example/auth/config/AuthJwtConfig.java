package org.example.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class AuthJwtConfig {

    @Value("${jwt.private-key-path}")
    private Resource privateKeyResource;

    @Value("${jwt.public-key-path}")
    private Resource publicKeyResource;

    @Bean
    public RSAKey rsaKey() throws Exception {
        // getInputStream()은 Resource 인터페이스에서 제공하는 메서드입니다.
        byte[] privateKeyBytes = privateKeyResource.getInputStream().readAllBytes();
        byte[] publicKeyBytes = publicKeyResource.getInputStream().readAllBytes();

        // 수정된 메서드 호출
        RSAPrivateKey privateKey = parsePrivateKey(new String(privateKeyBytes, StandardCharsets.UTF_8));
        RSAPublicKey publicKey = parsePublicKey(new String(publicKeyBytes, StandardCharsets.UTF_8));

        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("auth-key-id")
                .build();
    }

    // PKCS#1과 #8을 모두 수용하는 파싱 로직
    private RSAPrivateKey parsePrivateKey(String pem) throws Exception {
        try (PEMParser pemParser = new PEMParser(new StringReader(pem))) {
            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

            // 만료되었거나 규격이 다른 객체들을 PrivateKeyInfo로 변환하여 로드
            if (object instanceof org.bouncycastle.openssl.PEMKeyPair) {
                // PKCS#1 형식일 경우 여기서 처리됨
                return (RSAPrivateKey) converter.getPrivateKey(((org.bouncycastle.openssl.PEMKeyPair) object).getPrivateKeyInfo());
            } else if (object instanceof PrivateKeyInfo) {
                // PKCS#8 형식일 경우 여기서 처리됨
                return (RSAPrivateKey) converter.getPrivateKey((PrivateKeyInfo) object);
            }
            throw new IllegalArgumentException("지원하지 않는 개인키 형식입니다.");
        }
    }

    private RSAPublicKey parsePublicKey(String pem) throws Exception {
        try (PEMParser pemParser = new PEMParser(new StringReader(pem))) {
            Object object = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            return (RSAPublicKey) converter.getPublicKey((org.bouncycastle.asn1.x509.SubjectPublicKeyInfo) object);
        }
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (selector, ctx) -> selector.select(jwkSet);
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAKey rsaKey) throws Exception {
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
    }
}