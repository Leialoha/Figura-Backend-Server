package dev.leialoha.plugins.figuraserver.figura;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.google.gson.Gson;

import dev.leialoha.plugins.figuraserver.figura.types.AvatarMeta;
import dev.leialoha.plugins.figuraserver.figura.types.Releases;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;
import fi.iki.elonen.NanoHTTPD.Method;
import fi.iki.elonen.NanoHTTPD.Response;

public class FiguraHTTPResponses {
    private static final String UUID_REGEXP = "([\\da-f]{8}(?:-[\\da-f]{4}){4}[\\da-f]{8})";
    private static final String TEMP_UUID = "00000000-0000-0000-0000-000000000000";

    private static final AvatarManager avatarManager = new AvatarManager();
    private static final Releases figuraReleases = Releases.getInstance();
    private static final Gson gson = new Gson();

    public static Response serve(IHTTPSession session) {
        System.out.println(session.getMethod().toString() + ": " + session.getUri());
        String URI = session.getUri().replaceAll("\\/+", "/").replaceAll("^\\/|\\/$", "");
        String MATCHER_URI = URI.replaceAll(UUID_REGEXP, TEMP_UUID);

        return switch (MATCHER_URI) {
            case "api" -> checkSession(session);
            case "api/auth/id" -> authCreate(session);
            case "api/auth/verify" -> authVerify(session);
            case "api/version" -> new JSONResponse(gson.toJson(figuraReleases));
            case "api/limits" -> new JSONResponse("{\"limits\":{\"allowedBadges\":{\"pride\":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],\"special\":[1,1,1,1,1,1]},\"maxAvatarSize\":100000,\"maxAvatars\":10},\"rate\":{\"download\":50,\"equip\":1,\"pingRate\":32,\"pingSize\":1024,\"upload\":1}}");
            // case "api/motd" -> new JSONResponse("[{\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://github.com/figuramc/figura\"},\"text\":\"Please \"},{\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://github.com/figuramc/figura\"},\"color\":\"gold\",\"text\":\"Star\",\"underlined\":true},{\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://github.com/figuramc/figura\"},\"text\":\" us on GitHub!\\n\\n\"},{\"color\":\"red\",\"text\":\"0.1.4 released!!\\n\"},{\"color\":\"green\",\"text\":\"+1.16.5, 1.20.2 and 1.20.4 support\\n+ neoforge support\"}]");
            case "api/motd" -> new JSONResponse("[{\"text\": \"Please check out our discord at\"},{\"text\":\"[AHHHHHH]\",\"color\":\"blue\"}]");
            case "api/equip" -> applyAvatar(session);
            case "api/avatar" -> manageAvatar(session);
            case "api/" + TEMP_UUID -> retrieveAvatar(session);
            case "api/" + TEMP_UUID + "/avatar" -> downloadAvatar(session);
            default -> noResponse();
        };
    }

    
    private static Response noResponse() {
        System.out.println(" - no path");
        return new BasicResponse(Response.Status.OK, "");
    }

    private static Response checkSession(IHTTPSession session) {
        if (!isAuthorized(session)) return new InvalidSessionResponse();
        return new BasicResponse(Response.Status.OK, "ok");
    }

    private static Response retrieveAvatar(IHTTPSession session) {
        if (!isAuthorized(session)) return new InvalidSessionResponse();
        if (session.getMethod() == Method.GET) {
            String[] path = session.getUri().replaceAll("\\/+", "/").replaceAll("^\\/|\\/$", "").split("\\/+");
            UUID uuid = UUID.fromString(path[path.length - 1]);
            String avatarJSON = avatarManager.getAvatarDetails(uuid);

            return new JSONResponse(avatarJSON);
        } else return noResponse();
    }

    private static Response downloadAvatar(IHTTPSession session) {
        if (!isAuthorized(session)) return new InvalidSessionResponse();
        UUID uuid = getUUIDFromSession(session);
        if (session.getMethod() == Method.GET) {
            try {
                File avatarFile = avatarManager.getAvatarFile(uuid);
                return new FileResponse(avatarFile);
            } catch (IOException e) {
                e.printStackTrace();
                return new BasicResponse(Response.Status.BAD_REQUEST, "Error when sending file");
            }
        }
        return noResponse();
    }

    private static Response manageAvatar(IHTTPSession session) {
        if (!isAuthorized(session)) return new InvalidSessionResponse();
        UUID uuid = getUUIDFromSession(session);
        if (session.getMethod() == Method.DELETE) {
            avatarManager.deleteAvatar(uuid);
            return new BasicResponse(Response.Status.OK, "ok");
        } else if (session.getMethod() == Method.PUT) {
            try {

                int streamLength = Integer.parseInt(session.getHeaders().get("content-length"));
                byte[] fileContent = new byte[streamLength];

                InputStream input = session.getInputStream();
                int bytesRead = 0;
                while (bytesRead < streamLength) {
                    bytesRead += input.read(fileContent, bytesRead, streamLength - bytesRead);
                }

                avatarManager.uploadAvatar(uuid, fileContent);
                return new BasicResponse(Response.Status.OK, "ok");
            } catch (IOException e) {
                e.printStackTrace();
                return new BasicResponse(Response.Status.BAD_REQUEST, "Error when reading request body");
            }
        }
        return noResponse();
    }

    private static Response applyAvatar(IHTTPSession session) {
        if (!isAuthorized(session)) return new InvalidSessionResponse();
        UUID uuid = getUUIDFromSession(session);
        try {
            if (session.getMethod() == Method.POST) {
                int bytesRead = 0;
                int streamLength = Integer.parseInt(session.getHeaders().get("content-length"));
                InputStream input = session.getInputStream();
                byte[] bodyContent = new byte[streamLength];
                
                while (bytesRead < streamLength) {
                    bytesRead += input.read(bodyContent, bytesRead, streamLength - bytesRead);
                }

                String content = new String(bodyContent);
                AvatarMeta[] equippedAvatars = gson.fromJson(content, AvatarMeta[].class);
                avatarManager.equipAvatar(uuid, equippedAvatars);
                return new BasicResponse(Response.Status.OK, "ok");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new BasicResponse(Response.Status.BAD_REQUEST, "Error when reading request body");
        }
        return noResponse();
    }

    @SuppressWarnings("deprecation")
    private static Response authCreate(IHTTPSession session) {
        if (session.getMethod() == Method.GET) {
            try {
                String username = session.getParms().get("username");
                String sessionId = createAuthSession(username);
                return new BasicResponse(Response.Status.OK, sessionId);
            } catch (Exception e) {
                return new BasicResponse(Response.Status.BAD_REQUEST, e.getMessage());
            }
        }
        return noResponse();
    }

    @SuppressWarnings("deprecation")
    private static Response authVerify(IHTTPSession session) {
        if (session.getMethod() == Method.GET) {
            String sessionId = session.getParms().get("id");
            String username = getUsernameFromAuthSession(sessionId);

            try {
                String token = validateAuthSession(sessionId);
                removeAuthSession(username);

                return new BasicResponse(Response.Status.OK, token);
            } catch (Exception e) {
                removeAuthSession(username);
                return new BasicResponse(Response.Status.BAD_REQUEST, e.getMessage());
            }
        }
        return noResponse();
    }

    private static boolean isAuthorized(IHTTPSession session) {
        Map<String, String> headers = session.getHeaders();
        if (!headers.containsKey("token")) return false;
        String token = headers.get("token");
        return isSessionValid(token);
    }


    private static UUID getUUIDFromSession(IHTTPSession session) {
        String token = session.getHeaders().get("token");
        String username = getUsernameFromVerifiedSession(token);
        UUID uuid = Bukkit.getPlayer(username).getUniqueId();
        return uuid;
    }

    private static boolean isSessionValid(String sessionID) {
        return FiguraSessions.isSessionValid(sessionID);
    }

    private static String createAuthSession(String username) {
        return FiguraSessions.createAuthSession(username);
    }

    private static String removeAuthSession(String username) {
        return FiguraSessions.removeAuthSession(username);
    }

    private static String getUsernameFromAuthSession(String sessionID) {
        return FiguraSessions.getUsernameFromAuthSession(sessionID);
    }

    private static String getUsernameFromVerifiedSession(String sessionID) {
        return FiguraSessions.getUsernameFromVerifiedSession(sessionID);
    }

    private static String validateAuthSession(String sessionID) {
        return FiguraSessions.validateAuthSession(sessionID);
    }


    private static class InvalidSessionResponse extends Response {
        private static final String BAD_REQUEST = "Bad Request";

        protected InvalidSessionResponse() {
            super(Status.BAD_REQUEST, "text/plain", new ByteArrayInputStream(BAD_REQUEST.getBytes(StandardCharsets.UTF_8)), BAD_REQUEST.getBytes().length);
        }
    }

    private static class BasicResponse extends Response {
        protected BasicResponse(Status status, String message) {
            super(status, "text/plain", new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8)), message.getBytes().length);
        }
    }

    private static class JSONResponse extends Response {
        protected JSONResponse(String json) {
            super(Status.OK, "application/json; charset=utf-8", new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), json.getBytes().length);
        }
    }

    private static class FileResponse extends Response {
        protected FileResponse(File file) throws FileNotFoundException {
            super(Status.OK, "application/octet-stream", new FileInputStream(file), file.getTotalSpace());
        }
    }
}
