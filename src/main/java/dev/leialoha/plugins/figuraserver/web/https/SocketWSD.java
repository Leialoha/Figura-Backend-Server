package dev.leialoha.plugins.figuraserver.web.https;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import fi.iki.elonen.NanoWSD;

public class SocketWSD extends NanoWSD {
    private static final byte[] PING_PAYLOAD = "1337DEADBEEFC001".getBytes();
    private List<WebSocket> toAdd, toRemove;
    private Thread wsPinger;

    public SocketWSD(int port) {
        super(port);
        toRemove = new LinkedList<>();
        toAdd = new LinkedList<>();
    }

    public SocketWSD(String hostname, int port) {
        super(hostname, port);
        toRemove = new LinkedList<>();
        toAdd = new LinkedList<>();
    }

    @Override
    public void start() throws IOException {
        this.start(5000);
    }

    @Override
    public void start(int timeout) throws IOException {
        this.start(timeout, true);
    }

    @Override
    public void start(int timeout, boolean daemon) throws IOException {
        super.start(timeout, daemon);
        wsPinger = new Thread(new Runnable() {
            @Override
            public void run() {
                List<WebSocket> active = new LinkedList<>();
                long nextTime = System.currentTimeMillis();
                while (SocketWSD.this.isAlive()) {
                    nextTime += 4000L;
                    while (System.currentTimeMillis() < nextTime) {
                        try {
                            Thread.sleep(nextTime - System.currentTimeMillis());
                        } catch (InterruptedException ignored) {
                        }
                    }
                    synchronized (toAdd) {
                        active.addAll(toAdd);
                        toAdd.clear();
                    }
                    synchronized (toRemove) {
                        active.removeAll(toRemove);
                        toRemove.clear();
                        for (WebSocket ws : active) {
                            try {
                                ws.ping(PING_PAYLOAD);
                            } catch (Exception e) {
                                toRemove.add(ws);
                            }
                        }
                    }
                }
            }
        });
        wsPinger.setDaemon(true);
        wsPinger.start();
    }

    @Override
    protected final WebSocket openWebSocket(IHTTPSession ihttpSession) {
        WebSocket websocket = new WsdSocket(ihttpSession);
        return openWebSocket(websocket);
    }

    private final WebSocket openWebSocket(WebSocket socket) {
        synchronized (toAdd) {
            if (!toAdd.contains(socket))
                toAdd.add(socket);
        }
        return socket;
    }

    public final void onSocketClose(WebSocket socket) {
        synchronized (toRemove) {
            if (!toRemove.contains(socket))
                toRemove.add(socket);
        }
    }
}