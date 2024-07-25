package dev.leialoha.plugins.figuraserver.web.https;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import dev.leialoha.plugins.figuraserver.FiguraConfig;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class SSLUtil {

    public static SSLContext createSSLContext() throws Exception {
        // Initialize KeyStore
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        
        if (!FiguraConfig.USE_KEYSTORE_ENCRYPTION) {
            keyStore.load(null, null);

            // Load the certificate and private key
            X509Certificate[] certificateChain = loadCertificateChain(FiguraConfig.CERTIFICATE_FILE);
            PrivateKey privateKey = loadPrivateKey(FiguraConfig.PRIVATE_KEY_FILE);

            // Set private key and certificate in KeyStore
            keyStore.setKeyEntry("alias", privateKey, FiguraConfig.ENCRYPTION_PASS.toCharArray(), certificateChain);
        } else {
            FileInputStream fis = new FileInputStream(FiguraConfig.KEYSTORE_FILE);
            keyStore.load(fis, FiguraConfig.ENCRYPTION_PASS.toCharArray());
        }

        // Initialize KeyManagerFactory
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, FiguraConfig.ENCRYPTION_PASS.toCharArray());

        // Initialize SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

        return sslContext;
    }

    private static X509Certificate[] loadCertificateChain(String filePath) throws Exception {
        try (InputStream inputStream = new FileInputStream(filePath)) {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(inputStream);
            return new X509Certificate[] { cert };
        }
    }

    private static PrivateKey loadPrivateKey(String filePath) throws Exception {
        String pemContent = new String(Files.readAllBytes(Paths.get(filePath)));
        pemContent = pemContent.replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(pemContent);

        try (InputStream is = new ByteArrayInputStream(decoded)) {
            java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA");
            java.security.spec.PKCS8EncodedKeySpec keySpec = new java.security.spec.PKCS8EncodedKeySpec(decoded);
            return keyFactory.generatePrivate(keySpec);
        }
    }
}
