package dev.idhoorz.gpclaimblocks.listener;

import dev.idhoorz.gpclaimblocks.GPClaimBlocks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerJoinListener implements Listener {

    private final GPClaimBlocks plugin;

    public PlayerJoinListener(GPClaimBlocks plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                plugin.getRankManager().updateClaimBlocks(player);
            }
        }, 20L);
    }
}
