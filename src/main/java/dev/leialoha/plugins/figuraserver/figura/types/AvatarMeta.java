package dev.leialoha.plugins.figuraserver.figura.types;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.UUID;

import javax.annotation.Nullable;

public class AvatarMeta {
    public final String id = "avatar";
    public final UUID owner;
    public String hash;

    @Deprecated
    public AvatarMeta(String owner) {
        this.owner = UUID.fromString(owner);
    }

    public AvatarMeta(UUID owner) {
        this.owner = owner;
    }

    public AvatarMeta(UUID owner, String hash) {
        this.owner = owner;
        this.hash = hash;
    }

    public String getId() {
        return id;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Nullable
    public String getHash() {
        return hash;
    }

    public AvatarMeta generateHash(File file) {
        try {
            byte[] data = Files.readAllBytes(file.toPath());
            byte[] hash = MessageDigest.getInstance("MD5").digest(data);
            this.hash = new BigInteger(1, hash).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
}
