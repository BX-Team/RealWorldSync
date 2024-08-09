package space.bxteam.realworldsync;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import space.bxteam.realworldsync.commands.RWSCommand;
import space.bxteam.realworldsync.hooks.PlaceholderAPIHook;
import space.bxteam.realworldsync.managers.enums.Language;
import space.bxteam.realworldsync.utils.LogUtil;
import space.bxteam.realworldsync.utils.Metrics;
import space.bxteam.realworldsync.managers.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import space.bxteam.realworldsync.utils.UpdateCheckerUtil;

import java.io.File;

public class RealWorldSync extends JavaPlugin {
    private static RealWorldSync plugin;
    private File langFile;
    private FileConfiguration langConfig;

    public static RealWorldSync getInstance() {
        return RealWorldSync.plugin;
    }

    public FileConfiguration getLangConfig() {
        return langConfig;
    }

    @Override
    public void onEnable() {
        RealWorldSync.plugin = this;

        // Load config and messages
        saveDefaultConfig();
        createLangFile();
        Language.init(this);
        new RWSCommand().registerMainCommand(this, "rws");

        // Check for updates
        UpdateCheckerUtil.checkForUpdates().ifPresent(latestVersion -> {
            LogUtil.log("&2An update is available: " + latestVersion, LogUtil.LogLevel.INFO);
            LogUtil.log("&2Please update to the latest version to get bug fixes, security patches and new features!", LogUtil.LogLevel.INFO);
            LogUtil.log("&2Download here: https://modrinth.com/plugin/rws/version/" + latestVersion, LogUtil.LogLevel.INFO);
        });

        Metrics metrics = new Metrics(this, 19076);
        metrics.addCustomChart(new Metrics.SimplePie("time_sync_enabled", () -> String.valueOf(plugin.getConfig().getBoolean("time.enabled"))));
        metrics.addCustomChart(new Metrics.SimplePie("weather_sync_enabled", () -> String.valueOf(plugin.getConfig().getBoolean("weather.enabled"))));

        // Setting up time & weather synchronization
        if (plugin.getConfig().getBoolean("time.enabled")) {
            LogUtil.log("&eSetting up time synchronization...", LogUtil.LogLevel.INFO);
            WorldManager.setupTimeSynchronization();
        }
        if (plugin.getConfig().getBoolean("weather.enabled")) {
            LogUtil.log("&eSetting up weather synchronization...", LogUtil.LogLevel.INFO);
            WorldManager.setupWeatherSynchronization();
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            LogUtil.log("&eRegistering placeholder expansion...", LogUtil.LogLevel.INFO);
            new PlaceholderAPIHook().register();
        }

        LogUtil.log("Plugin loaded!", LogUtil.LogLevel.INFO);
    }

    @Override
    public void onDisable() {
        LogUtil.log("&eCancelling all tasks", LogUtil.LogLevel.INFO);
        Bukkit.getScheduler().cancelTasks(this);

        if (plugin.getConfig().getBoolean("time.enabled")) {
            LogUtil.log("&eEnabling Minecraft default daylight cycle", LogUtil.LogLevel.INFO);
            WorldManager.enableDaylightCycle();
        }
        if (plugin.getConfig().getBoolean("weather.enabled")) {
            LogUtil.log("&eEnabling Minecraft default weather cycle", LogUtil.LogLevel.INFO);
            WorldManager.enableWeatherCycle();
        }
    }

    public void unload() {
        Bukkit.getScheduler().cancelTasks(this);
        if (plugin.getConfig().getBoolean("time.enabled")) {
            WorldManager.enableDaylightCycle();
        }
        if (plugin.getConfig().getBoolean("weather.enabled")) {
            WorldManager.enableWeatherCycle();
        }
    }

    public void load() {
        reloadConfig();
        createLangFile();
        Language.init(this);

        if (plugin.getConfig().getBoolean("time.enabled")) {
            WorldManager.setupTimeSynchronization();
        }
        if (plugin.getConfig().getBoolean("weather.enabled")) {
            WorldManager.setupWeatherSynchronization();
        }
    }

    public void reload() {
        this.unload();
        this.load();
    }

    private void createLangFile() {
        langFile = new File(getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            saveResource("lang.yml", false);
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }
}
