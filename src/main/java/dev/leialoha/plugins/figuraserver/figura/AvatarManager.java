package dev.leialoha.plugins.figuraserver.figura;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.UUID;

import com.google.gson.Gson;

import dev.leialoha.plugins.figuraserver.FiguraServer;
import dev.leialoha.plugins.figuraserver.figura.types.Avatar;
import dev.leialoha.plugins.figuraserver.figura.types.AvatarMeta;

public class AvatarManager {
    
    private final Gson gson = new Gson();
    private final HashMap<UUID, Avatar> avatars = new HashMap<>();

    public String getAvatarDetails(UUID uuid) {

        try {
            if (avatars.containsKey(uuid)) {
                return gson.toJson(avatars.get(uuid));
            } else if (hasUser(uuid)) {
                File file = getAvatar(uuid, "json", false);
                InputStream inputStream = new FileInputStream(file);
                String content = new String(inputStream.readAllBytes());
                inputStream.close();

                // Store into temp memory
                Avatar avatar = gson.fromJson(content, Avatar.class);
                avatars.put(uuid, avatar);

                return content;
            } else if (hasAvatar(uuid)) {
                Avatar avatar = new Avatar(uuid);
                AvatarMeta equipped = new AvatarMeta(uuid).generateHash(getAvatar(uuid, "avtr"));
                avatar.setEquipped(new AvatarMeta[] { equipped });

                // Store into temp memory
                avatars.put(uuid, avatar);

                return gson.toJson(avatar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }

    public File getAvatarFile(UUID uuid) {
        return getAvatar(uuid, "avtr");
    }

    public void uploadAvatar(UUID uuid, byte[] data) throws IOException {
        Files.write(getAvatar(uuid, "avtr").toPath(), data);
    }

    public void deleteAvatar(UUID uuid) {
        try {
            Avatar avatar;
            if (avatars.containsKey(uuid)) {
                avatar = avatars.get(uuid);
            } else if (hasUser(uuid)) {
                File file = getAvatar(uuid, "json", false);
                InputStream inputStream = new FileInputStream(file);
                String content = new String(inputStream.readAllBytes());
                inputStream.close();

                // Store into temp memory
                avatar = gson.fromJson(content, Avatar.class);
                avatars.put(uuid, avatar);
            } else return;

            for (AvatarMeta equippedMeta : avatar.getEquipped()) {
                if (equippedMeta.getOwner().equals(uuid)) {
                    try {
                        File avatarFile = getAvatar(equippedMeta.getOwner(), "avtr");
                        Files.delete(avatarFile.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            avatar.setEquipped(new AvatarMeta[0]);

            // Save to disk
            FileOutputStream outputStream = new FileOutputStream(getAvatar(uuid, "json"));
            outputStream.write(gson.toJson(avatar).getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void equipAvatar(UUID uuid, AvatarMeta[] equippedAvatars) {
        try {
            Avatar avatar;
            if (avatars.containsKey(uuid)) {
                avatar = avatars.get(uuid);
            } else if (hasUser(uuid)) {
                File file = getAvatar(uuid, "json", false);
                InputStream inputStream = new FileInputStream(file);
                String content = new String(inputStream.readAllBytes());
                inputStream.close();
    
                // Store into temp memory
                avatar = gson.fromJson(content, Avatar.class);
                avatars.put(uuid, avatar);
            } else {
                avatar = new Avatar(uuid);
                avatars.put(uuid, avatar);
            }

            // Generate avatar hash
            for (AvatarMeta avatarMeta : equippedAvatars) {
                UUID avatarUUID = avatarMeta.getOwner();
                File avatarFile = getAvatar(avatarUUID, "avtr");
                avatarMeta.generateHash(avatarFile);
            }
            avatar.setEquipped(equippedAvatars);

            // Save to disk
            FileOutputStream outputStream = new FileOutputStream(getAvatar(uuid, "json"));
            outputStream.write(gson.toJson(avatar).getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean hasUser(UUID uuid) {
        return getAvatar(uuid, "json", false).exists();
    }

    private boolean hasAvatar(UUID uuid) {
        return getAvatar(uuid, "avtr", false).exists();
    }

    private File getAvatar(UUID uuid, String type) {
        return getAvatar(uuid, type, true);
    }

    private File getAvatar(UUID uuid, String type, boolean create) {
        String name = uuid.toString() + "." + type;
        File avatarFolder = new File(FiguraServer.INSTANCE.getDataFolder(), "avatars");
        File sortedFolder = new File(avatarFolder, name.substring(0, 2));
        if (!sortedFolder.exists() && create) sortedFolder.mkdirs();
        return new File(sortedFolder, name);
    }


}
