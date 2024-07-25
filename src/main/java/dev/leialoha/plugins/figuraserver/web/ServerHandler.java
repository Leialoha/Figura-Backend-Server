package dev.leialoha.plugins.figuraserver.web;

import java.nio.channels.AlreadyBoundException;

import dev.leialoha.plugins.figuraserver.FiguraConfig;
import dev.leialoha.plugins.figuraserver.web.https.HttpsServerWithSockets;

public class ServerHandler {
    
    private static AlreadyBoundException EXCEPTION = null;
    private HttpsServerWithSockets httpServer;
    
    public ServerHandler() {
        if (EXCEPTION != null) throw EXCEPTION;
        EXCEPTION = new AlreadyBoundException();

        // Load configuration
        httpServer = new HttpsServerWithSockets(FiguraConfig.SERVER_HOST, FiguraConfig.SERVER_PORT);
    }

    public void startServer() {
        try {
            try {
                httpServer.makeSecure(new String[] { "TLSv1", "TLSv1.1", "TLSv1.2" });
            } catch (Exception e) {
                throw new RuntimeException("Failed to create SSL context for HTTP server", e);
            }
            httpServer.start(0, false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to start HTTPS server", e);
        }

    }

    public void stopServer() {
        if (httpServer.isAlive()) httpServer.stop();
    }

}
