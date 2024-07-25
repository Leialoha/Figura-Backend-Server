package dev.leialoha.plugins.figuraserver.web.https;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import dev.leialoha.plugins.figuraserver.figura.FiguraWebsocketHandler;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoWSD.WebSocket;
import fi.iki.elonen.NanoWSD.WebSocketFrame;
import fi.iki.elonen.NanoWSD.WebSocketFrame.CloseCode;

public class WsdSocket extends WebSocket implements FiguraWebsocketHandler {
    private boolean verified = false;
    private String sessionId = null;
    private int ping = 0;
    private int pong = 0;

    public final byte AUTH = 0;
    public final byte PING = 1;
    public final byte EVENT = 2;
    public final byte TOAST = 3;
    public final byte CHAT = 4;
    public final byte NOTICE = 5;

    protected WsdSocket(IHTTPSession handshakeRequest) {
        super(handshakeRequest);
    }

    @Override
    protected void onMessage(WebSocketFrame webSocketFrame) {

        byte[] payloadWithMessage = webSocketFrame.getBinaryPayload();
        ByteArrayInputStream s = new ByteArrayInputStream(payloadWithMessage);
        byte type = (byte) s.read();
        byte[] message = s.readAllBytes();

        try {
            switch (type) {
                case AUTH -> readAuthMessage(this, message);
                case PING -> readPingMessage(this, message);
                case EVENT -> readEventMessage(this, message);
                case TOAST -> readToastMessage(this, message);
                case CHAT -> readChatMessage(this, message);
                case NOTICE -> readNoticeMessage(this, message);
                default -> this.close(CloseCode.GoingAway, "Invalid message payload.", false);
            }
        } catch (Exception ignored) {}

        // if (!verified) {
        //     try {
        //         activateSession(message);
        //         sessionId = message;
        //         System.out.println(webSocketFrame.isFin());
        //         // this.send("\u0000");
        //     } catch (Exception ignored) {
        //     }
        // }

        // try {
        //     if (!verified)
        //         this.close(CloseCode.GoingAway, "Unauthorized web socket.", false);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }

    @Override
    protected void onClose(CloseCode closeReason, String reason, boolean initiatedByRemote) {
        if (sessionId != null) deactivateSession(sessionId);

        System.out.println("onClose");
        System.out.println(" - " + closeReason.toString());
        System.out.println(" - " + reason);
        System.out.println(" - " + initiatedByRemote);
    }

    @Override
    protected void onException(IOException exception) {

    }

    @Override
    protected void onOpen() {

    }

    @Override
    protected void onPong(WebSocketFrame webSocketFrame) {
        this.pong++;
    }

    @Override
    public void ping(byte[] payload) throws IOException {
        super.ping(payload);
        if(this.ping++ - this.pong > 3) close(CloseCode.GoingAway, "Missed too many ping requests.", false);
    }

    public void setVerified() {
        this.verified = true;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}