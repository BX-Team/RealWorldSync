package space.bxteam.realworldsync.commands.subcommands;

import org.bukkit.command.CommandSender;
import space.bxteam.realworldsync.RealWorldSync;
import space.bxteam.realworldsync.managers.command.SubCommand;
import space.bxteam.realworldsync.managers.enums.Language;
import space.bxteam.realworldsync.utils.TextUtils;
import space.bxteam.realworldsync.utils.UpdateCheckerUtil;

import java.util.List;

public class VersionCommand implements SubCommand {
    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getDescription() {
        return "Shows the plugin version and sends message if there is an update available";
    }

    @Override
    public String getSyntax() {
        return "/rws version";
    }

    @Override
    public String getPermission() {
        return "rws.user";
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        return List.of();
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        sender.sendMessage(Language.PREFIX.asColoredString() + TextUtils.applyColor("&aCurrent installed version: " + RealWorldSync.getInstance().getPluginMeta().getVersion()));
        UpdateCheckerUtil.checkForUpdates().ifPresent(latestVersion -> {
            sender.sendMessage(Language.PREFIX.asColoredString() + TextUtils.applyColor("&aA new update is available: " + latestVersion));
            sender.sendMessage(Language.PREFIX.asColoredString() + TextUtils.applyColor("&aDownload here: &ehttps://modrinth.com/plugin/rws/version/" + latestVersion));
        });
    }
}
