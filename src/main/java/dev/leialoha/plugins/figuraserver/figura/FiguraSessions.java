package dev.leialoha.plugins.figuraserver.figura;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import dev.leialoha.plugins.figuraserver.reflection.ReflectionUtils;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

class FiguraSessions {
    private static final Set<String> activeSessions = new HashSet<>();
    private static final Map<String, String> verifiedSessions = new HashMap<>();
    private static final Map<String, String> authSessions = new HashMap<>();
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private FiguraSessions() {}

    protected static String createAuthSession(String username) {
        if (username == null) throw new IllegalArgumentException("Bad request");
        String sessionID = generateHex();
        authSessions.put(username, sessionID);
        return sessionID;
    }

    private static String generateId() {
        char[] alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 32; i++) {
            stringBuilder.append(alphanumeric[(int) Math.floor(Math.random() * alphanumeric.length)]);
        }

        return stringBuilder.toString();
    }

    private static String generateHex() {
        byte[] bytes = new byte[19];
        new Random().nextBytes(bytes);
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    protected static String removeAuthSession(String username) {
        return authSessions.remove(username);
    }

    private static String getUsernameFromSession(String sessionID, Map<String, String> sessions) {
        if (sessionID == null) throw new IllegalArgumentException("Bad request");
        Optional<Entry<String, String>> entry = sessions.entrySet().stream()
            .filter(e -> e.getValue().equals(sessionID)).findFirst();

        if (entry.isEmpty()) throw new IllegalArgumentException("Invalid session id");
        return entry.get().getKey();
    }

    protected static String getUsernameFromAuthSession(String sessionID) {
        return getUsernameFromSession(sessionID, authSessions);
    }

    protected static String getUsernameFromVerifiedSession(String sessionID) {
        return getUsernameFromSession(sessionID, verifiedSessions);
    }

    protected static String validateAuthSession(String sessionID) {
        String username = getUsernameFromSession(sessionID, authSessions);

        try {
            Class<?> craftServerClass = ReflectionUtils.getOBCClass("CraftServer");
            Class<?> minecraftServerClass = ReflectionUtils.getNMSClass("net.minecraft.server.MinecraftServer");
            Class<?> gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
            Class<?> minecraftSessionServiceClass = Class.forName("com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService");
            Constructor<?> gameProfileConstructor = gameProfileClass.getConstructor(UUID.class, String.class);
            Object tempGameProfile = gameProfileConstructor.newInstance(UUID.randomUUID(), username);

            Object server = craftServerClass.cast(Bukkit.getServer());
            Object dedicatedServer = ReflectionUtils.useMethod("getServer", server);
            Object minecraftServer = minecraftServerClass.cast(dedicatedServer);
            Object minecraftSessionService = ReflectionUtils.useMethod(minecraftServerClass, "am", minecraftServer);

            Method hasJoinedServer = ReflectionUtils.getMethod(minecraftSessionServiceClass, "hasJoinedServer",
                    gameProfileClass, String.class, InetAddress.class);

            Object gameProfile = hasJoinedServer.invoke(minecraftSessionService, tempGameProfile, sessionID, null);
            String name = (String) ReflectionUtils.useMethod("getName", gameProfile);

            if (!name.equals(username))
                throw new IllegalArgumentException("Names do not match");

            String newSession = generateId();
            if (verifiedSessions.containsKey(username))
                deactivateSessionByUsername(username);
            verifiedSessions.put(username, newSession);

            return newSession;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Failed to verify", e);
        }
    }

    protected static boolean isSessionValid(String sessionID) {
        return activeSessions.contains(sessionID) && verifiedSessions.containsValue(sessionID);
    }

    protected static void activateSession(String sessionID) {
        if (!activeSessions.add(sessionID) || !verifiedSessions.containsValue(sessionID)) {
            deactivateSession(sessionID);
            throw new IllegalArgumentException("This session is already in use");
        }
    }

    protected static void deactivateSessionByUsername(String username) {
        String sessionID = verifiedSessions.get(username);
        verifiedSessions.remove(username, sessionID);
        activeSessions.remove(sessionID);
    }

    protected static void deactivateSession(String sessionID) {
        try {
            String username = getUsernameFromSession(sessionID, verifiedSessions);
            verifiedSessions.remove(username, sessionID);
            activeSessions.remove(sessionID);
        } catch (IllegalArgumentException ignored) {}
    }
}
