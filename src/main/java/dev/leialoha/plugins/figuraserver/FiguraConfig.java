package dev.leialoha.plugins.figuraserver;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import org.bukkit.configuration.file.FileConfiguration;

public class FiguraConfig {

    // Figura Settings
    public static boolean CHECK_FOR_UPDATES = true;
    public static float UPDATE_INTERVAL = 60 * 60;

    public static int DEFAULT_AVATAR_PERMISSION = 2;
    public static int OPERATOR_AVATAR_PERMISSION = 3;

    public static boolean REMOVE_AVATAR_ON_BAN = true;
    public static boolean REMOVE_AVATAR_ON_INACTIVE = true;
    public static float AVATAR_INACTIVE_TIMER = 2 * 7 * 60 * 60;
    public static List<String> WHITELISTED_AVATARS = new ArrayList<>();

    // Server Settings
    public static String SERVER_HOST = null;
    public static int SERVER_PORT = 443;

    public static String ENCRYPTION_PASS = "password";
    public static boolean USE_SSL_ENCRYPTION = true;
    public static boolean USE_KEYSTORE_ENCRYPTION = false;

    public static String CERTIFICATE_FILE = null;
    public static String PRIVATE_KEY_FILE = null;

    public static String KEYSTORE_FILE = null;
    public static String KEYSTORE_PASSWORD = "changeit";

    public static void loadConfig(FileConfiguration configuration) {
        final String NAME = FiguraServer.INSTANCE.getName();

        CHECK_FOR_UPDATES = configuration.getBoolean("figura.check-for-updates", true);
        UPDATE_INTERVAL = convertStringToTime(configuration.getString("figura.update-interval", "1h"), "1h");
        DEFAULT_AVATAR_PERMISSION = configuration.getInt("figura.permissions.default-level", 2);
        OPERATOR_AVATAR_PERMISSION = configuration.getInt("figura.permissions.op-level", 3);
        REMOVE_AVATAR_ON_BAN = configuration.getBoolean("figura.avatars.remove-when-banned", true);
        REMOVE_AVATAR_ON_INACTIVE = configuration.getBoolean("figura.avatars.remove-when-inactive", true);
        AVATAR_INACTIVE_TIMER = convertStringToTime(configuration.getString("figura.avatars.inactive-timer", "2W"), "2W");
        WHITELISTED_AVATARS = configuration.getStringList("figura.avatars.ignore-avatars");
        SERVER_HOST = configuration.getString("server.host", null);
        SERVER_PORT = configuration.getInt("server.port", 443);
        ENCRYPTION_PASS = configuration.getString("server.password", "password");
        USE_SSL_ENCRYPTION = configuration.getBoolean("server.use-ssl-encryption", true);
        USE_KEYSTORE_ENCRYPTION = configuration.getBoolean("server.use-keystore-encryption", false);
        CERTIFICATE_FILE = configuration.getString("server.encryption.x509.certificate", "./plugins/" + NAME + "/certificates/cert.pem");
        PRIVATE_KEY_FILE = configuration.getString("server.encryption.x509.private-key", "./plugins/" + NAME + "/certificates/privkey.pem");
        KEYSTORE_FILE = configuration.getString("server.encryption.keystore.file", "./plugins/" + NAME + "/certificates/keystore.jks");
    }

    private static final float convertStringToTime(String valueToConvert, String defaultValue) {
        List<MatchResult> results = Pattern.compile("((\\d+)([YMWDhms]))").matcher(valueToConvert).results().toList();
        if (results.size() == 0 && defaultValue != null) return convertStringToTime(defaultValue, null);
        else if (results.size() == 0) return -1;

        float out = 0F;

        for (MatchResult result : results) {
            float delay = Integer.parseInt(result.group(2));

            // Yes I'm too lazy to implement this correctly
            switch (result.group(3)) {
                case "Y": delay *= 12 * (365.24F / 360);
                case "M": delay *= 30; // Better off doing 1Y than 12M
                case "D": delay *= 24;
                case "h": delay *= 60;
                case "m": delay *= 60;
                case "s": out += delay; break;
                case "W": out += (delay * 7 * 24 * 60 * 60); break;
            }

            out = out + delay;
        }

        return out;
    }

}
