package dev.leialoha.plugins.figuraserver.figura.types;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;

import dev.leialoha.plugins.figuraserver.FiguraConfig;

public class Avatar {

    public AvatarMeta[] equipped;
    public final AllowedBadges equippedBadges;

    public final UUID uuid;
    public String rank;
    public Date lastUsed;
    public String version;

    public boolean banned;
    public int trust;

    public Avatar(UUID uuid) {
        this(uuid, "default", new AllowedBadges(), new AvatarMeta[0], getTempVersion(), Date.from(Instant.now()));
    }

    public Avatar(UUID uuid, String rank) {
        this(uuid, rank, new AllowedBadges(), new AvatarMeta[0], getTempVersion(), Date.from(Instant.now()));
    }

    public Avatar(UUID uuid, AllowedBadges equippedBadges) {
        this(uuid, "default", equippedBadges, new AvatarMeta[0], getTempVersion(), Date.from(Instant.now()));
    }

    public Avatar(UUID uuid, AllowedBadges equippedBadges, AvatarMeta[] equipped) {
        this(uuid, "default", equippedBadges, equipped, getTempVersion(), Date.from(Instant.now()));
    }

    public Avatar(UUID uuid, String rank, AllowedBadges equippedBadges) {
        this(uuid, rank, equippedBadges, new AvatarMeta[0], getTempVersion(), Date.from(Instant.now()));
    }

    public Avatar(UUID uuid, String rank, AllowedBadges equippedBadges, AvatarMeta[] equipped) {
        this(uuid, rank, equippedBadges, equipped, getTempVersion(), Date.from(Instant.now()));
    }

    public Avatar(UUID uuid, String rank, AllowedBadges equippedBadges, AvatarMeta[] equipped, String version) {
        this(uuid, rank, equippedBadges, equipped, version, Date.from(Instant.now()));
    }

    public Avatar(UUID uuid, String rank, AllowedBadges equippedBadges, AvatarMeta[] equipped, String version, Date lastUsed) {
        boolean isOpped = Bukkit.getOperators().stream().anyMatch(p -> p.getUniqueId().equals(uuid));

        this.uuid = uuid;
        this.rank = rank;
        this.lastUsed = lastUsed;
        this.version = version;

        this.equippedBadges = equippedBadges;
        this.equipped = equipped;

        this.banned = false;
        this.trust = Math.max(FiguraConfig.DEFAULT_AVATAR_PERMISSION,
            isOpped ? FiguraConfig.OPERATOR_AVATAR_PERMISSION : 0);
    }

    public static String getTempVersion() {
        return Releases.getInstance().release + "+" + Bukkit.getVersion();
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public void setEquipped(AvatarMeta[] equipped) {
        this.equipped = equipped;
    }

    public void setLastUsed() {
        this.lastUsed = Date.from(Instant.now());
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setTrust(int trust) {
        this.trust = trust;
    }


    public UUID getUniqueId() {
        return uuid;
    }

    public AvatarMeta[] getEquipped() {
        return equipped;
    }

    public AllowedBadges getEquippedBadges() {
        return equippedBadges;
    }

    public Date getLastUsed() {
        return lastUsed;
    }

    public String getRank() {
        return rank;
    }

    public String getVersion() {
        return version;
    }

    public boolean isBanned() {
        return banned;
    }

    public int getTrust() {
        return trust;
    }
}
