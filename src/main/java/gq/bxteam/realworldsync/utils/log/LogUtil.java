package gq.bxteam.realworldsync.utils.log;

import gq.bxteam.realworldsync.RealWorldSync;
import org.bukkit.ChatColor;

public class LogUtil {
    private static final RealWorldSync plugin = RealWorldSync.getPlugin();

    public static void sendConsoleLog(final String msg, final LogType type) {
        String out = type.color() + "[" + type.name() + "] &aRealWorldSync: " + ChatColor.GRAY + msg;
        out = ChatColor.translateAlternateColorCodes('&', out);
        LogUtil.plugin.getServer().getConsoleSender().sendMessage(out);
    }
}
