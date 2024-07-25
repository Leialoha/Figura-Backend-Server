package dev.leialoha.plugins.figuraserver.figura;

public interface FiguraServerHandler {
    
    default void activateSession(String sessionID) {
        FiguraSessions.activateSession(sessionID);
    }

    default void deactivateSession(String sessionID) {
        FiguraSessions.deactivateSession(sessionID);
    }
}
