package space.bxteam.realworldsync.managers;

import space.bxteam.realworldsync.RealWorldSync;
import space.bxteam.realworldsync.utils.LogUtil;
import space.bxteam.realworldsync.utils.OpenWeatherMapUtil;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.concurrent.CompletableFuture;

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
                Calendar calendar = Calendar.getInstance(ConfigManager.getTimezoneFromConfig());
                for (World world : getServer().getWorlds()) {
                    if (world.getEnvironment().equals(World.Environment.NORMAL)) {
                        world.setTime((1000L * calendar.get(Calendar.HOUR_OF_DAY)) + (16 * (calendar.get(Calendar.MINUTE) + 1)) - 6000);
                    }
                }
            }
        }.runTaskTimer(RealWorldSync.getInstance(), 0L, ConfigManager.time_update);
    }

    public static void setupWeatherSynchronization() {
        try {
            new OpenWeatherMapUtil(ConfigManager.weather_owm_key, ConfigManager.weather_latitude, ConfigManager.weather_longitude);
        } catch (Exception ex) {
            LogUtil.log("&cFailed to start weather synchronization", LogUtil.LogLevel.ERROR);
            LogUtil.log("Error code: " + ex.getMessage(), LogUtil.LogLevel.ERROR);
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
                CompletableFuture.supplyAsync(() -> {
                    try {
                        OpenWeatherMapUtil util = new OpenWeatherMapUtil(ConfigManager.weather_owm_key, ConfigManager.weather_latitude, ConfigManager.weather_longitude);
                        return util;
                    } catch (Exception ex) {
                        LogUtil.log("There was a problem getting the weather data", LogUtil.LogLevel.ERROR);
                        return null;
                    }
                }).thenAcceptAsync(util -> {
                    if (util != null) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (World world : getServer().getWorlds()) {
                                    if (world.getEnvironment().equals(World.Environment.NORMAL)) {
                                        world.setStorm(OpenWeatherMapUtil.isRaining());
                                        world.setThundering(OpenWeatherMapUtil.isThunder());
                                    }
                                }
                            }
                        }.runTask(RealWorldSync.getInstance());
                    }
                });
            }
        }.runTaskTimer(RealWorldSync.getInstance(), 0L, ConfigManager.weather_update);
    }

    public static void enableDaylightCycle() {
        for (World world : getServer().getWorlds()) {
            if (world.getEnvironment().equals(World.Environment.NORMAL)) {
                if (ConfigManager.time_enabled) world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            }
        }
    }

    public static void enableWeatherCycle() {
        for (World world : getServer().getWorlds()) {
            if (world.getEnvironment().equals(World.Environment.NORMAL)) {
                if (ConfigManager.time_enabled) {
                    world.setGameRule(GameRule.DO_WEATHER_CYCLE, true);
                    world.setStorm(false);
                    world.setThundering(false);
                }
            }
        }
    }

    @Contract(" -> new")
    public static @NotNull ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.ofInstant(Instant.now(), ConfigManager.getTimezoneFromConfig().toZoneId());
    }
}
