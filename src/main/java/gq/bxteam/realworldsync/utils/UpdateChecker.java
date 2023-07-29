package gq.bxteam.realworldsync.utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Automatic update checker. Fetches version from the declared url and returning true/false
 * <p>
 * You can found original version at <a href="https://github.com/Elytrium/java-commons/blob/master/utils/src/main/java/net/elytrium/commons/utils/updates/UpdatesChecker.java">java-commons library</a>
 */
public class UpdateChecker {
    /**
     * Checks the difference between the current version and the version from the declared url.
     *
     * @param URL A URL that contains latest version (for example, GitHub RAW)
     * @param currentVersion Current version of installed plugin.
     * @return Return true if currentVersion is newer or equals to latest
     */
    public static boolean fetchVersionFromGithub(@NotNull String URL, @NotNull String currentVersion) {
        try {
            URLConnection urlConnection = new URL(URL).openConnection();
            int timeout = (int) TimeUnit.SECONDS.toMillis(5L);
            urlConnection.setConnectTimeout(timeout);
            urlConnection.setReadTimeout(timeout);
            return splitFileVersion(new Scanner(urlConnection.getInputStream(), StandardCharsets.UTF_8).nextLine().trim(), currentVersion);
        } catch (IOException ex) {
            throw new UncheckedIOException("Unable to check for updates!", ex);
        }
    }

    public static boolean splitFileVersion(String latestVersion, String currentVersion) {
        String[] latestParts = latestVersion.split("-")[0].split("\\.");
        String[] currentParts = currentVersion.split("-")[0].split("\\.");

        String latest = latestVersion.replaceAll("\\D+", "");
        String current = currentVersion.replaceAll("\\D+", "");

        StringBuilder sbLatest =
                new StringBuilder(String.format("%0" + latest.length() + "d", Long.parseLong(latest) - (latestVersion.contains("-") ? 1 : 0)));
        StringBuilder sbCurrent =
                new StringBuilder(String.format("%0" + current.length() + "d", Long.parseLong(current) - (currentVersion.contains("-") ? 1 : 0)));

        int padding = 0;

        for (int i = 0; i < Math.max(latestParts.length, currentParts.length); i++) {
            String latestPart = i < latestParts.length ? latestParts[i] : "";
            String currentPart = i < currentParts.length ? currentParts[i] : "";
            int maxLength = Math.max(latestPart.length(), currentPart.length());
            int toAddLatest = maxLength - latestPart.length();
            int toAddCurrent = maxLength - currentPart.length();

            sbLatest.insert(padding, new String(new char[toAddLatest]).replace('\0', '0'));
            sbCurrent.insert(padding, new String(new char[toAddCurrent]).replace('\0', '0'));

            padding += maxLength;
        }

        long latestId = Long.parseLong(sbLatest.toString());
        long currentId = Long.parseLong(sbCurrent.toString());

        return currentId >= latestId;
    }
}
