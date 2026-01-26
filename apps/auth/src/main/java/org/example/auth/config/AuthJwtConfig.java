package org.example.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource; // 필수: 이 Resource여야 합니다.
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class AuthJwtConfig {

    @Value("${jwt.private-key-path}")
    private Resource privateKeyResource;

    @Value("${jwt.public-key-path}")
    private Resource publicKeyResource;

    @Bean
    public RSAKey rsaKey() throws Exception {
        // getInputStream()은 Resource 인터페이스에서 제공하는 메서드입니다.
        String privateKeyPem = new String(privateKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String publicKeyPem = new String(publicKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        // 아래에 정의한 메서드를 호출합니다.
        RSAPrivateKey privateKey = readPrivateKey(privateKeyPem);
        RSAPublicKey publicKey = readPublicKey(publicKeyPem);

        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID("auth-key-id")
                .build();
    }

    // 직접 작성해야 하는 파싱 메서드입니다.
    private RSAPrivateKey readPrivateKey(String pem) throws Exception {
        String key = pem.replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] encoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }

    // 직접 작성해야 하는 파싱 메서드입니다.
    private RSAPublicKey readPublicKey(String pem) throws Exception {
        String key = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] encoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
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
}