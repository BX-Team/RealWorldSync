package space.bxteam.realworldsync.hooks;

import space.bxteam.realworldsync.RealWorldSync;
import space.bxteam.realworldsync.managers.WorldManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    @Override
    public @NotNull String getAuthor() {
        return RealWorldSync.getInstance().getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "rws";
    }

    @Override
    public @NotNull String getVersion() {
        return RealWorldSync.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (identifier.equalsIgnoreCase("current_world_time_hhmm")) {
            ZonedDateTime zonedDateTime = WorldManager.getZonedDateTime();
            return zonedDateTime.getHour() + ":" + zonedDateTime.getMinute();
        }
        if (identifier.equalsIgnoreCase("current_world_time_hhmmss")) {
            ZonedDateTime zonedDateTime = WorldManager.getZonedDateTime();
            return zonedDateTime.getHour() + ":" + zonedDateTime.getMinute() + ":" + zonedDateTime.getSecond();
        }

        return null;
    }
}
