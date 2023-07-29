package gq.bxteam.realworldsync.cmds;

import gq.bxteam.realworldsync.RealWorldSync;
import gq.bxteam.realworldsync.config.Language;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VersionCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!sender.hasPermission("rws.user")) {
            sender.sendMessage(Language.msg_prefix + Language.msg_no_perms);
            return true;
        } else if (sender instanceof Player) {
            sender.sendMessage(Language.msg_prefix + "Current installed plugin version: " + RealWorldSync.getPlugin().getDescription().getVersion());
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return List.of("version");
    }
}
