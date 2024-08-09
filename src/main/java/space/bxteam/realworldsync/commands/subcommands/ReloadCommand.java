package space.bxteam.realworldsync.commands.subcommands;

import org.bukkit.command.CommandSender;
import space.bxteam.realworldsync.RealWorldSync;
import space.bxteam.realworldsync.managers.command.SubCommand;
import space.bxteam.realworldsync.managers.enums.Language;

import java.util.List;

public class ReloadCommand implements SubCommand {
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the plugin";
    }

    @Override
    public String getSyntax() {
        return "/rws reload";
    }

    @Override
    public String getPermission() {
        return "rws.admin";
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        try {
            RealWorldSync.getInstance().reload();
            sender.sendMessage(Language.PREFIX.asColoredString() + Language.COMMANDS_RELOAD.asColoredString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
