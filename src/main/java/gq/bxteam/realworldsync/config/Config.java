package gq.bxteam.realworldsync.config;

import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
    public static final YamlConfiguration cfg = new YamlConfiguration();
    public static boolean time_enabled;
    public static String time_world;
    public static int time_update;
    public static String time_timezone;

    public static boolean weather_enabled;
    public static String weather_owm_key;
    public static String weather_city;
    public static String weather_country;

    public static void setupConfig() {
        cfg.addDefault();
    }
}
