package gq.bxteam.realworldsync.cmds;

import gq.bxteam.realworldsync.RealWorldSync;
import gq.bxteam.realworldsync.config.Config;
import gq.bxteam.realworldsync.config.Language;
import gq.bxteam.realworldsync.world.WorldManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String @NotNull [] args) {
        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "help" -> {
                    if (sender.hasPermission("rws.user")) {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(Language.msg_help));
                    } else {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(Language.msg_prefix + Language.msg_no_perms));
                        return false;
                    }
                }

                case "time" -> {
                    if (sender.hasPermission("rws.user")) {
                        if (Config.time_enabled) {
                            ZonedDateTime zonedDateTime = WorldManager.getZonedDateTime();
                            String time = zonedDateTime.getHour() + ":" + zonedDateTime.getMinute();
                            sender.sendMessage(MiniMessage.miniMessage().deserialize(Language.msg_prefix + Language.msg_time, Placeholder.parsed("time", "<gold>" + time)));
                        } else {
                            sender.sendMessage(MiniMessage.miniMessage().deserialize(Language.msg_prefix + Language.msg_time_disabled));
                            return false;
                        }
                    } else {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(Language.msg_prefix + Language.msg_no_perms));
                        return false;
                    }
                }

                case "reload" -> {
                    if (sender.hasPermission("rws.admin")) {
                        RealWorldSync.getPlugin().reload();
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(Language.msg_prefix + Language.msg_reload_complete));
                    } else {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(Language.msg_prefix + Language.msg_no_perms));
                        return false;
                    }
                }

                case "version" -> {
                    if (sender.hasPermission("rws.user")) {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(Language.msg_prefix + Language.msg_version, Placeholder.parsed("version", "<green>" + RealWorldSync.getPlugin().getDescription().getVersion())));
                        RealWorldSync.checkForUpdates().ifPresent(latestVersion -> {
                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>An update is available: " + latestVersion));
                            sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Download here: https://modrinth.com/plugin/rws/version/" + latestVersion));
                        });
                    } else {
                        sender.sendMessage(MiniMessage.miniMessage().deserialize(Language.msg_prefix + Language.msg_no_perms));
                        return false;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {
        if (args.length == 1) {
            return Arrays.asList("help", "time", "reload", "version");
        }

        return new ArrayList<>();
    }
}
