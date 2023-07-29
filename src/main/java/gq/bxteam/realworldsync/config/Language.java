package gq.bxteam.realworldsync.config;

import gq.bxteam.realworldsync.RealWorldSync;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Language {
    private static final YamlConfiguration cfg = new YamlConfiguration();
    public static String msg_help;
    public static String msg_prefix;
    public static String msg_no_perms;
    public static String msg_version;
    public static String msg_time;
    public static String msg_time_disabled;
    public static String msg_reload_complete;

    public static void setupMessages() {
        File file = new File(RealWorldSync.getPlugin().getDataFolder(), "lang.yml");
        if (file.exists()) {
            try {
                cfg.load(file);
            } catch (IOException | InvalidConfigurationException e) {
                RealWorldSync.getPlugin().getLogger().log(Level.SEVERE, "Could not load lang.yml!", e);
                return;
            }
        }

        cfg.addDefault("Help", "<gray>-----------<gray> [<green>RealWorldSync<gray>] -----------<newline><dark_green>» <green>/rws time <gray>- Shows time by timezone in config.<newline><dark_green>» <green>/rws reload <gray>- Reload the plugin.<newline><dark_green>» <green>/rws version <gray>- Shows the version currently installed");
        cfg.addDefault("Prefix", "<gray>[<green>RealWorldSync<gray>] <gray>");
        cfg.addDefault("NoPermission", "<red>You don't have permissions to do that.");
        cfg.addDefault("Version", "<green>Current installed plugin version: <version>");
        cfg.addDefault("Time", "<gold>Current world time: <time>");
        cfg.addDefault("TimeDisabled", "<red>Time is disabled in the config!");
        cfg.addDefault("ReloadComplete", "<green>Reload complete!");
        cfg.options().copyDefaults(true);
        try {
            cfg.save(file);
        } catch (Exception e) {
            RealWorldSync.getPlugin().getLogger().log(Level.WARNING, "Failed to save lang.yml!");
        }

        Language.msg_help = cfg.getString("Help");
        Language.msg_prefix = cfg.getString("Prefix");
        Language.msg_no_perms = cfg.getString("NoPermission");
        Language.msg_version = cfg.getString("Version");
        Language.msg_time = cfg.getString("Time");
        Language.msg_time_disabled = cfg.getString("TimeDisabled");
        Language.msg_reload_complete = cfg.getString("ReloadComplete");
    }
}
