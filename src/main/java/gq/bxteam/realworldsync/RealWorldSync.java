package gq.bxteam.realworldsync;

import gq.bxteam.realworldsync.cmds.Commands;
import gq.bxteam.realworldsync.config.Config;
import gq.bxteam.realworldsync.config.Language;
import gq.bxteam.realworldsync.hooks.PlaceholderAPIHook;
import gq.bxteam.realworldsync.utils.UpdateChecker;
import gq.bxteam.realworldsync.utils.log.LogType;
import gq.bxteam.realworldsync.utils.log.LogUtil;
import gq.bxteam.realworldsync.utils.metrics.Metrics;
import gq.bxteam.realworldsync.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RealWorldSync extends JavaPlugin {
    private static RealWorldSync plugin;

    public static RealWorldSync getPlugin() {
        return RealWorldSync.plugin;
    }

    @Override
    public void onEnable() {
        RealWorldSync.plugin = this;

        // Load config and messages
        this.saveDefaultConfig();
        Config.setupConfig();
        Language.setupMessages();
        getCommand("rws").setExecutor(new Commands());
        getCommand("rws").setTabCompleter(new Commands());

        // Check for updates
        if (Config.opt_check_for_updates) {
            if (!UpdateChecker.fetchVersionFromGithub("https://raw.githubusercontent.com/BX-Team/RealWorldSync/master/VERSION", RealWorldSync.getPlugin().getDescription().getVersion())) {
                LogUtil.sendConsoleLog("The new update of RealWorldSync was found! We recommend to update here: https://modrinth.com/plugin/rws", LogType.WARN);
            }
        }

        // Metrics initialize
        if (Config.opt_enable_metrics) {
            LogUtil.sendConsoleLog("&aEnabling metrics", LogType.INFO);
            Metrics metrics = new Metrics(this, 19076);
            metrics.addCustomChart(new Metrics.SimplePie("time_sync_enabled", () -> String.valueOf(Config.time_enabled)));
            metrics.addCustomChart(new Metrics.SimplePie("weather_sync_enabled", () -> String.valueOf(Config.weather_enabled)));
        }

        // Setting up time & weather synchronization
        if (Config.time_enabled) {
            LogUtil.sendConsoleLog("&eSetting up time synchronization...", LogType.INFO);
            WorldManager.setupTimeSynchronization();
        }
        if (Config.weather_enabled) {
            LogUtil.sendConsoleLog("&eSetting up weather synchronization...", LogType.INFO);
            WorldManager.setupWeatherSynchronization();
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            LogUtil.sendConsoleLog("&eRegistering placeholder expansion...", LogType.INFO);
            new PlaceholderAPIHook().register();
        }

        LogUtil.sendConsoleLog("Plugin loaded!", LogType.INFO);
    }

    @Override
    public void onDisable() {
        LogUtil.sendConsoleLog("&eCancelling all tasks", LogType.INFO);
        Bukkit.getScheduler().cancelTasks(this);

        if (Config.time_enabled) {
            LogUtil.sendConsoleLog("&eEnabling Minecraft default daylight cycle", LogType.INFO);
            WorldManager.enableDaylightCycle();
        }
        if (Config.weather_enabled) {
            LogUtil.sendConsoleLog("&eEnabling Minecraft default weather cycle", LogType.INFO);
            WorldManager.enableWeatherCycle();
        }
    }

    public void unload() {
        Bukkit.getScheduler().cancelTasks(this);
        if (Config.time_enabled) {
            WorldManager.enableDaylightCycle();
        }
        if (Config.weather_enabled) {
            WorldManager.enableWeatherCycle();
        }
    }

    public void load() {
        Config.setupConfig();
        Language.setupMessages();

        if (Config.time_enabled) {
            WorldManager.setupTimeSynchronization();
        }
        if (Config.weather_enabled) {
            WorldManager.setupWeatherSynchronization();
        }
    }

    public void reload() {
        this.unload();
        this.load();
    }
}
