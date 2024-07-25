package dev.leialoha.plugins.figuraserver.web.https;

import java.io.IOException;

import javax.net.ssl.SSLServerSocketFactory;

import dev.leialoha.plugins.figuraserver.FiguraConfig;
import dev.leialoha.plugins.figuraserver.figura.FiguraHTTPResponses;
import dev.leialoha.plugins.figuraserver.figura.FiguraServerHandler;

// Custom HTTP server class extending NanoHTTPD
public class HttpsServerWithSockets extends SocketWSD implements FiguraServerHandler {

    public HttpsServerWithSockets(String hostname, int port) {
        super(hostname, port);
        System.out.println("Starting server...");
    }

    public HttpsServerWithSockets makeSecure(String[] sslProtocols) throws Exception {
        System.out.println("Using SSL encryption...");
        if (FiguraConfig.USE_SSL_ENCRYPTION) {
            SSLServerSocketFactory sslServerSocketFactory = SSLUtil.createSSLContext().getServerSocketFactory();
            super.makeSecure(sslServerSocketFactory, sslProtocols);
        } else {
            System.out.println("SSL encryption disabled by config! Skipping...");
        }
        return this;
    }

    @Override
    public void start(int timeout, boolean daemon) throws IOException {
        super.start(timeout, daemon);
        System.out.println("HTTPS server listening on port " + this.getListeningPort());
    }

    @Override
    public Response serve(IHTTPSession session) {
        if ("GET".equalsIgnoreCase(session.getMethod().name()) && "/ws".equalsIgnoreCase(session.getUri())) {
            if (!session.getHeaders().containsKey("upgrade") || !session.getHeaders().get("upgrade").equalsIgnoreCase("websocket")) {
                String location = "wss://" + session.getHeaders().get("Host") + session.getUri();
                Response response = newFixedLengthResponse(Response.Status.SWITCH_PROTOCOL, "text/plain", "Switching to WebSocket protocol");
                response.addHeader("Location", location);
                response.addHeader("Upgrade", "websocket");
                response.addHeader("Connection", "Upgrade");
                return response;
            } else return super.serve(session);
        }

        return FiguraHTTPResponses.serve(session);
    }

}
