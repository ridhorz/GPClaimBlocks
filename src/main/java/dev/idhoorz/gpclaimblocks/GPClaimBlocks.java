package dev.idhoorz.gpclaimblocks;

import dev.idhoorz.gpclaimblocks.commands.MainCommand;
import dev.idhoorz.gpclaimblocks.hooks.PlaceholderHook;
import dev.idhoorz.gpclaimblocks.listeners.PlayerListener;
import dev.idhoorz.gpclaimblocks.managers.RankManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventSubscription;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class GPClaimBlocks extends JavaPlugin {

    private static GPClaimBlocks instance;
    private RankManager rankManager;
    private EventSubscription<UserDataRecalculateEvent> lpSubscription;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        this.rankManager = new RankManager(this);

        hookLuckPerms();
        hookPlaceholderAPI();

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        MainCommand cmd = new MainCommand(this);
        getCommand("gpclaimblocks").setExecutor(cmd);
        getCommand("gpclaimblocks").setTabCompleter(cmd);

        getLogger().info("Loaded " + rankManager.getRankCount() + " ranks.");
    }

    @Override
    public void onDisable() {
        if (lpSubscription != null) lpSubscription.close();
    }

    private void hookLuckPerms() {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) return;

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider == null) return;

        lpSubscription = provider.getProvider().getEventBus()
                .subscribe(this, UserDataRecalculateEvent.class, e -> {
                    Bukkit.getScheduler().runTask(this, () -> {
                        Player p = Bukkit.getPlayer(e.getUser().getUniqueId());
                        if (p != null && p.isOnline()) rankManager.updateClaimBlocks(p);
                    });
                });

        getLogger().info("Hooked into LuckPerms.");
    }

    private void hookPlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) return;
        new PlaceholderHook(this).register();
        getLogger().info("Hooked into PlaceholderAPI.");
    }

    public void reload() {
        reloadConfig();
        rankManager.reload();
        Bukkit.getOnlinePlayers().forEach(rankManager::updateClaimBlocks);
        getLogger().info("Reloaded " + rankManager.getRankCount() + " ranks.");
    }

    public void debug(String msg) {
        if (getConfig().getBoolean("debug")) getLogger().info("[DEBUG] " + msg);
    }

    public static GPClaimBlocks getInstance() {
        return instance;
    }

    public RankManager getRankManager() {
        return rankManager;
    }
}
