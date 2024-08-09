package space.bxteam.realworldsync.commands.subcommands;

import org.bukkit.command.CommandSender;
import space.bxteam.realworldsync.managers.ConfigManager;
import space.bxteam.realworldsync.managers.WorldManager;
import space.bxteam.realworldsync.managers.command.SubCommand;
import space.bxteam.realworldsync.managers.enums.Language;

import java.time.ZonedDateTime;
import java.util.List;

public class TimeCommand implements SubCommand {
    @Override
    public String getName() {
        return "time";
    }

    @Override
    public String getDescription() {
        return "Shows current world time (from timezone)";
    }

    @Override
    public String getSyntax() {
        return "/rws time";
    }

    @Override
    public String getPermission() {
        return "rws.user";
    }

    @Override
    public List<String> getTabCompletion(CommandSender sender, int index, String[] args) {
        return null;
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (ConfigManager.time_enabled) {
            ZonedDateTime zonedDateTime = WorldManager.getZonedDateTime();
            String time = zonedDateTime.getHour() + ":" + zonedDateTime.getMinute();
            sender.sendMessage(Language.PREFIX.asColoredString() + Language.COMMANDS_TIME_CURRENT.asColoredString().replace("<time>", time));
        } else {
            sender.sendMessage(Language.PREFIX.asColoredString() + Language.COMMANDS_TIME_DISABLED.asColoredString());
        }
    }
}
