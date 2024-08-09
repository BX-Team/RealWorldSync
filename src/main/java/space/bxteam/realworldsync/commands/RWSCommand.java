package space.bxteam.realworldsync.commands;

import org.bukkit.command.CommandSender;
import space.bxteam.realworldsync.commands.subcommands.*;
import space.bxteam.realworldsync.managers.command.MainCommand;
import space.bxteam.realworldsync.managers.command.matcher.StringArgumentMatcher;
import space.bxteam.realworldsync.managers.enums.Language;

public class RWSCommand extends MainCommand {
    public RWSCommand() {
        super(new StringArgumentMatcher());
    }

    @Override
    protected void registerSubCommands() {
        subCommands.add(new HelpCommand());
        subCommands.add(new ReloadCommand());
        subCommands.add(new TimeCommand());
        subCommands.add(new VersionCommand());
    }

    @Override
    protected void perform(CommandSender sender) {
        for (String message : Language.COMMANDS_HELP.asColoredStringList()) {
            sender.sendMessage(message);
        }
    }
}
