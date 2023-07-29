package gq.bxteam.realworldsync.config;

import gq.bxteam.realworldsync.RealWorldSync;
import gq.bxteam.realworldsync.utils.log.LogType;
import gq.bxteam.realworldsync.utils.log.LogUtil;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Level;

public class Config {
    private static final YamlConfiguration cfg = new YamlConfiguration();
    public static boolean check_for_updates;
    public static boolean enable_metrics;
    public static boolean time_enabled;
    public static String time_world;
    public static int time_update;
    public static String time_timezone;
    private static TimeZone timeZone;

    public static boolean weather_enabled;
    public static int weather_update;
    public static String weather_owm_key;
    public static String weather_latitude;
    public static String weather_longitude;

    public static void setupConfig() {
        File file = new File(RealWorldSync.getPlugin().getDataFolder(), "config.yml");
        if (file.exists()) {
            try {
                cfg.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                RealWorldSync.getPlugin().getLogger().log(Level.SEVERE, "Could not load config.yml!", e);
                return;
            }
        }

        Config.check_for_updates = cfg.getBoolean("check-for-updates");
        Config.enable_metrics = cfg.getBoolean("enable-metrics");

        Config.time_enabled = cfg.getBoolean("time.enabled");
        Config.time_world = cfg.getString("time.world");
        Config.time_update = cfg.getInt("time.update-interval");
        Config.time_timezone = cfg.getString("time.timezone");
        if (Config.time_enabled) setTimeZone(Config.time_timezone);

        Config.weather_enabled = cfg.getBoolean("weather.enabled");
        Config.weather_update = cfg.getInt("weather.update-interval");
        Config.weather_owm_key = cfg.getString("weather.openweathermap-key");
        Config.weather_latitude = cfg.getString("weather.location.latitude");
        Config.weather_longitude = cfg.getString("weather.location.longitude");
    }

    public static TimeZone getTimezoneFromConfig() {
        return timeZone;
    }

    public static void setTimeZone(String value) {
        try {
            timeZone = TimeZone.getTimeZone(ZoneId.of(Objects.requireNonNull(value)));
        } catch (ZoneRulesException | NullPointerException e) {
            LogUtil.sendConsoleLog("&cTimezone invalid!", LogType.ERROR);
        }
    }
}
