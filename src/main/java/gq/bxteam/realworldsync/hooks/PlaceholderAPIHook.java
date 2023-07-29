package gq.bxteam.realworldsync.hooks;

import gq.bxteam.realworldsync.RealWorldSync;
import gq.bxteam.realworldsync.world.WorldManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    @Override
    public @NotNull String getAuthor() {
        return RealWorldSync.getPlugin().getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "rws";
    }

    @Override
    public @NotNull String getVersion() {
        return RealWorldSync.getPlugin().getDescription().getVersion();
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
        if (identifier.equalsIgnoreCase("current_world_time")) {
            ZonedDateTime zonedDateTime = WorldManager.getZonedDateTime();
            return zonedDateTime.getHour() + ":" + zonedDateTime.getMinute();
        }

        return null;
    }
}
