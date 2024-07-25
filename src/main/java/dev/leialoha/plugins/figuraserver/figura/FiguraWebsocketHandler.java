package dev.leialoha.plugins.figuraserver.figura;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import dev.leialoha.plugins.figuraserver.web.https.WsdSocket;
import fi.iki.elonen.NanoWSD.WebSocketFrame.CloseCode;
public interface FiguraWebsocketHandler extends FiguraServerHandler {
    default void readAuthMessage(WsdSocket websocket, byte[] payload) {
        try {
            try {
                String message = new String(payload, "UTF-8");
                activateSession(message);
                websocket.setSessionId(message);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(baos);
                dos.writeByte(websocket.AUTH);
                dos.close();
                websocket.send(baos.toByteArray());
            } catch (Exception ioException) {
                ioException.printStackTrace();
                websocket.close(CloseCode.GoingAway, "Unauthorized web socket.", false);
            }
        } catch (IOException ioException) {
            System.out.println("Couldn't close socket");
            ioException.printStackTrace();
        }
    }

    default void readPingMessage(WsdSocket websocket, byte[] payload) {

    }

    default void readEventMessage(WsdSocket websocket, byte[] payload) {

    }

    default void readToastMessage(WsdSocket websocket, byte[] payload) {

    }

    default void readChatMessage(WsdSocket websocket, byte[] payload) {

    }

    default void readNoticeMessage(WsdSocket websocket, byte[] payload) {

    }

}
