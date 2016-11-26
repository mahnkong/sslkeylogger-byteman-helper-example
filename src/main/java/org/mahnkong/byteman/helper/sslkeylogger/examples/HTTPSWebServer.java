package org.mahnkong.byteman.helper.sslkeylogger.examples;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.util.Headers;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Date;

/**
 * Created by mahnkong on 26.11.2016.
 */
public class HTTPSWebServer {
    private static final String keystorePassword = "geheim";
    private static final String truststorePassword = "geheim";

    public static void main(final String[] args) {
        Undertow.Builder builder = Undertow.builder();
        builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true);
        try {
            KeyStore keyStore = loadKeyStore("build/resources/main/serverkeystore.jks", keystorePassword);
            KeyStore trustStore = loadKeyStore("build/resources/main/servertruststore.jks", truststorePassword);
            SSLContext sslContext = createSSLContext(keyStore, trustStore);
            builder.addHttpsListener(443, "0.0.0.0", sslContext).setHandler(exchange -> {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                exchange.getResponseSender().send("Hello World via SSL (Date = " + new Date().toString() + ")");
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup an Undertow SSL listener!", e);
        }

        Undertow server = builder.build();
        server.start();
    }

    private static SSLContext createSSLContext(final KeyStore keyStore, final KeyStore trustStore) throws Exception {
        KeyManager[] keyManagers;
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keystorePassword.toCharArray());
        keyManagers = keyManagerFactory.getKeyManagers();

        TrustManager[] trustManagers = null;
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory
                .getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);
        trustManagers = trustManagerFactory.getTrustManagers();

        SSLContext sslContext;
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);

        System.err.println("SSLContext: " + sslContext);

        return sslContext;
    }

    private static KeyStore loadKeyStore(String filename, String password) throws Exception {
        KeyStore loadedKeystore = KeyStore.getInstance("JKS");
        File file = new File(filename);
        if (file.exists()) {
            try (InputStream stream = new FileInputStream(file)) {
                loadedKeystore.load(stream, password.toCharArray());
            }
        } else {
            throw new IllegalStateException(String.format("Failed to find keystore '%s'!", filename));
        }
        return loadedKeystore;
    }
}
