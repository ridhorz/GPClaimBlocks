package dev.idhoorz.gpclaimblocks.hook;

import dev.idhoorz.gpclaimblocks.GPClaimBlocks;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventSubscription;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class LuckPermsHook {

    private final GPClaimBlocks plugin;
    private EventSubscription<UserDataRecalculateEvent> subscription;

    public LuckPermsHook(GPClaimBlocks plugin) {
        this.plugin = plugin;
    }

    public boolean setup() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) return false;

        LuckPerms api = provider.getProvider();
        subscription = api.getEventBus().subscribe(plugin, UserDataRecalculateEvent.class, this::onRankChange);
        return true;
    }

    private void onRankChange(UserDataRecalculateEvent event) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Player player = Bukkit.getPlayer(event.getUser().getUniqueId());
            if (player != null && player.isOnline()) {
                plugin.getRankManager().updateClaimBlocks(player);
            }
        });
    }

    public void unregister() {
        if (subscription != null) subscription.close();
    }
}
