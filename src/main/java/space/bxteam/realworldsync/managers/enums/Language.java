package space.bxteam.realworldsync.managers.enums;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import space.bxteam.realworldsync.RealWorldSync;
import space.bxteam.realworldsync.utils.TextUtils;

import java.util.List;
import java.util.stream.Collectors;

public enum Language {
    PREFIX("prefix"),
    NO_PERMISSION("no-permission"),

    COMMANDS_HELP("commands.help"),
    COMMANDS_TIME_CURRENT("commands.time.current"),
    COMMANDS_TIME_DISABLED("commands.time.disabled"),
    COMMANDS_RELOAD("commands.reload");

    private final String path;
    private static FileConfiguration langConfig;

    Language(String path) {
        this.path = path;
    }

    public static void init(@NotNull RealWorldSync plugin) {
        langConfig = plugin.getLangConfig();
    }

    public String asString() {
        return langConfig.getString(this.path);
    }

    public String asColoredString() {
        return TextUtils.applyColor(asString());
    }

    public List<String> asStringList() {
        return langConfig.getStringList(this.path);
    }

    public List<String> asColoredStringList() {
        return asStringList().stream()
                .map(TextUtils::applyColor)
                .collect(Collectors.toList());
    }
}
