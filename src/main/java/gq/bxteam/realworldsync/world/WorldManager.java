package gq.bxteam.realworldsync.world;

import gq.bxteam.realworldsync.RealWorldSync;
import gq.bxteam.realworldsync.config.Config;
import gq.bxteam.realworldsync.utils.OpenWeatherMapUtil;
import gq.bxteam.realworldsync.utils.log.LogType;
import gq.bxteam.realworldsync.utils.log.LogUtil;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Calendar;

import static org.bukkit.Bukkit.getServer;

public class WorldManager {
    public static void setupTimeSynchronization() {
        for (World world : getServer().getWorlds()) {
            if (world.getEnvironment().equals(World.Environment.NORMAL)) {
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance(Config.getTimezoneFromConfig());
                for (World world : getServer().getWorlds()) {
                    if (world.getEnvironment().equals(World.Environment.NORMAL)) {
                        world.setTime((1000L * calendar.get(Calendar.HOUR_OF_DAY)) + (16 * (calendar.get(Calendar.MINUTE) + 1)) - 6000);
                    }
                }
            }
        }.runTaskTimerAsynchronously(RealWorldSync.getPlugin(), 0L, Config.time_update);
    }

    public static void setupWeatherSynchronization() {
        try {
            new OpenWeatherMapUtil(Config.weather_owm_key, Config.weather_latitude, Config.weather_longitude);
        } catch (Exception ex) {
            LogUtil.sendConsoleLog("&cFailed to start weather synchronization", LogType.ERROR);
            LogUtil.sendConsoleLog("Error code: " + ex.getMessage(), LogType.ERROR);
            return;
        }

        for (World world : getServer().getWorlds()) {
            if (world.getEnvironment().equals(World.Environment.NORMAL)) {
                world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    OpenWeatherMapUtil util = new OpenWeatherMapUtil(Config.weather_owm_key, Config.weather_latitude, Config.weather_longitude);
                    for (World world : getServer().getWorlds()) {
                        if (world.getEnvironment().equals(World.Environment.NORMAL)) {
                            world.setStorm(OpenWeatherMapUtil.isRaining());
                            world.setThundering(OpenWeatherMapUtil.isThunder());
                        }
                    }
                } catch (Exception ex) {
                    LogUtil.sendConsoleLog("There was a problem getting the weather data", LogType.ERROR);
                }
            }
        }.runTaskTimerAsynchronously(RealWorldSync.getPlugin(), 0L, Config.weather_update);
    }

    public static void enableDaylightCycle() {
        for (World world : getServer().getWorlds()) {
            if (world.getEnvironment().equals(World.Environment.NORMAL)) {
                if (Config.time_enabled) world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            }
        }
    }

    public static void enableWeatherCycle() {
        for (World world : getServer().getWorlds()) {
            if (world.getEnvironment().equals(World.Environment.NORMAL)) {
                if (Config.time_enabled) {
                    world.setGameRule(GameRule.DO_WEATHER_CYCLE, true);
                    world.setStorm(false);
                    world.setThundering(false);
                }
            }
        }
    }

    @Contract(" -> new")
    public static @NotNull ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.ofInstant(Instant.now(), Config.getTimezoneFromConfig().toZoneId());
    }
}
