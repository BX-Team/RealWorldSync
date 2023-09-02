package gq.bxteam.realworldsync;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gq.bxteam.realworldsync.cmds.Commands;
import gq.bxteam.realworldsync.config.Config;
import gq.bxteam.realworldsync.config.Language;
import gq.bxteam.realworldsync.hooks.PlaceholderAPIHook;
import gq.bxteam.realworldsync.utils.log.LogType;
import gq.bxteam.realworldsync.utils.log.LogUtil;
import gq.bxteam.realworldsync.utils.metrics.Metrics;
import gq.bxteam.realworldsync.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

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
        checkForUpdates().ifPresent(latestVersion -> {
            LogUtil.sendConsoleLog("&2An update is available: " + latestVersion, LogType.INFO);
            LogUtil.sendConsoleLog("&2Please update to the latest version to get bug fixes, security patches and new features!", LogType.INFO);
            LogUtil.sendConsoleLog("&2Download here: https://modrinth.com/plugin/rws/version/" + latestVersion, LogType.INFO);
        });

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

    public static Optional<String> checkForUpdates() {
        final String mcVersion = RealWorldSync.getPlugin().getServer().getMinecraftVersion();
        final String pluginName = RealWorldSync.getPlugin().getPluginMeta().getName();
        final String pluginVersion = RealWorldSync.getPlugin().getPluginMeta().getVersion();
        try {
            final HttpClient client = HttpClient.newHttpClient();
            final HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.modrinth.com/v2/project/1BY2Jy64/version?featured=true&game_versions=[%22" + mcVersion + "%22]"))
                    .header("User-Agent",
                            pluginName + "/" + pluginVersion
                    )
                    .GET()
                    .build();
            final HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() < 400 && res.statusCode() >= 200 && res.body() != null) {
                final JsonObject json = JsonParser.parseString(res.body()).getAsJsonArray().get(0).getAsJsonObject();
                if (json.has("version_number")) {
                    final String latestVersion = json.get("version_number").getAsString();
                    if (!latestVersion.equals(pluginVersion))
                        return Optional.of(latestVersion);
                }
            }
        }
        catch (final Exception e) {
            LogUtil.sendConsoleLog("Failed to check for updates: " + e, LogType.ERROR);
        }
        return Optional.empty();
    }
}
