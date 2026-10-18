package com.globalfintech.globalfinancialengine.application.security;

import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

@Service
public class DigitalSignatureService {
    private final KeyPair keyPair;

    public DigitalSignatureService() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            this.keyPair = generator.generateKeyPair();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudieron crear las claves", e);
        }
    }

    public String sign(String content) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(keyPair.getPrivate());
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo firmar el documento", e);
        }
    }

    public boolean verify(String content, String signedValue) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(keyPair.getPublic());
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return signature.verify(Base64.getDecoder().decode(signedValue));
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            return false;
        }
    }
}
