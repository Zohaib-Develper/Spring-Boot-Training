package com.training.lecture02;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class KeyTest {
    public static void main(String[] args) throws Exception {
        String pem = new String(Files.readAllBytes(Paths.get("/home/zohaib.musharaf/Documents/Training/lecture02/src/test/resources/private_key.pem")));
        pem = pem.replaceAll("-----BEGIN (.*)-----", "").replaceAll("-----END (.*)-----", "").replaceAll("\\s", "");
        System.out.println("Base64 length: " + pem.length());
        byte[] decoded = Base64.getDecoder().decode(pem);
        System.out.println("Decoded length: " + decoded.length);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateKey key = (RSAPrivateKey) kf.generatePrivate(spec);
        System.out.println("OK: " + key.getFormat());
    }
}
