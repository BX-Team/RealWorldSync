package space.bxteam.realworldsync.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import space.bxteam.realworldsync.RealWorldSync;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class UpdateCheckerUtil {
    public static Optional<String> checkForUpdates() {
        final String mcVersion = RealWorldSync.getInstance().getServer().getMinecraftVersion();
        final String pluginName = RealWorldSync.getInstance().getPluginMeta().getName();
        final String pluginVersion = RealWorldSync.getInstance().getPluginMeta().getVersion();
        try {
            final HttpClient client = HttpClient.newHttpClient();
            final HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.modrinth.com/v2/project/1BY2Jy64/version?featured=true&game_versions=[%22" + mcVersion + "%22]"))
                    .header("User-Agent",
                            pluginName + "/" + pluginVersion
                    )
                    .GET()
                    .build();
            final HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() < 400 && res.statusCode() >= 200 && res.body() != null) {
                final JsonObject json = JsonParser.parseString(res.body()).getAsJsonArray().get(0).getAsJsonObject();
                if (json.has("version_number")) {
                    final String latestVersion = json.get("version_number").getAsString();
                    if (!latestVersion.equals(pluginVersion))
                        return Optional.of(latestVersion);
                }
            }
        }
        catch (final Exception e) {
            LogUtil.log("Failed to check for updates: " + e, LogUtil.LogLevel.ERROR);
        }
        return Optional.empty();
    }
}
