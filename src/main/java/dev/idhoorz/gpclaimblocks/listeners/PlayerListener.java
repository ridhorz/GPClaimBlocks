package dev.idhoorz.gpclaimblocks.listeners;

import dev.idhoorz.gpclaimblocks.GPClaimBlocks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerListener implements Listener {

    private final GPClaimBlocks plugin;

    public PlayerListener(GPClaimBlocks plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) plugin.getRankManager().updateClaimBlocks(player);
        }, 20L);
    }
}
