package dev.leialoha.plugins.figuraserver.figura.types;

public class Limits {
    public final Limit limits = new Limit();
    public final Rate rate = new Rate();

    class Limit {
        public final AllowedBadges allowedBadges = new AllowedBadges();

        int maxAvatarSize = 100000;
        int maxAvatars = 10;
    }

    class Rate {
        int download = 50;
        int equip = 1;
        int pingRate = 32;
        int pingSize = 1024;
        int upload = 1;
    }

}
