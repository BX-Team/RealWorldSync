package space.bxteam.realworldsync.managers;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import space.bxteam.realworldsync.RealWorldSync;
import space.bxteam.realworldsync.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Level;

public class ConfigManager {
    private static final YamlConfiguration cfg = new YamlConfiguration();
    public static boolean opt_enable_metrics;
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
        File file = new File(RealWorldSync.getInstance().getDataFolder(), "config.yml");
        if (file.exists()) {
            try {
                cfg.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                RealWorldSync.getInstance().getLogger().log(Level.SEVERE, "Could not load config.yml!", e);
                return;
            }
        }

        ConfigManager.opt_enable_metrics = cfg.getBoolean("options.enable-metrics");

        ConfigManager.time_enabled = cfg.getBoolean("time.enabled");
        ConfigManager.time_world = cfg.getString("time.world");
        ConfigManager.time_update = cfg.getInt("time.update-interval");
        ConfigManager.time_timezone = cfg.getString("time.timezone");
        if (ConfigManager.time_enabled) setTimeZone(ConfigManager.time_timezone);

        ConfigManager.weather_enabled = cfg.getBoolean("weather.enabled");
        ConfigManager.weather_update = cfg.getInt("weather.update-interval");
        ConfigManager.weather_owm_key = cfg.getString("weather.openweathermap-key");
        ConfigManager.weather_latitude = cfg.getString("weather.location.latitude");
        ConfigManager.weather_longitude = cfg.getString("weather.location.longitude");
    }

    public static TimeZone getTimezoneFromConfig() {
        return timeZone;
    }

    public static void setTimeZone(String value) {
        try {
            timeZone = TimeZone.getTimeZone(ZoneId.of(Objects.requireNonNull(value)));
        } catch (ZoneRulesException | NullPointerException e) {
            LogUtil.log("&cTimezone invalid!", LogUtil.LogLevel.ERROR);
        }
    }
}
