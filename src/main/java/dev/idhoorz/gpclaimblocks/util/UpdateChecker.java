package dev.idhoorz.gpclaimblocks.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class UpdateChecker {

    private final JavaPlugin plugin;
    private final int resourceId;

    public UpdateChecker(JavaPlugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void check() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::fetchLatestVersion);
    }

    private void fetchLatestVersion() {
        try {
            URL url = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String latest = reader.readLine();
                String current = plugin.getDescription().getVersion();

                if (latest != null && !current.equals(latest)) {
                    plugin.getLogger().info("Update available: " + latest + " (current: " + current + ")");
                    plugin.getLogger().info("Download: https://www.spigotmc.org/resources/" + resourceId);
                }
            }
        } catch (Exception ignored) {
        }
    }
}
