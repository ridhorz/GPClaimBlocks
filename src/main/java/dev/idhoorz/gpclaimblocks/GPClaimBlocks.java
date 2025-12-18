package dev.idhoorz.gpclaimblocks;

import dev.idhoorz.gpclaimblocks.command.CommandHandler;
import dev.idhoorz.gpclaimblocks.hook.LuckPermsHook;
import dev.idhoorz.gpclaimblocks.hook.PlaceholderHook;
import dev.idhoorz.gpclaimblocks.hook.VaultHook;
import dev.idhoorz.gpclaimblocks.listener.PlayerJoinListener;
import dev.idhoorz.gpclaimblocks.manager.RankManager;
import dev.idhoorz.gpclaimblocks.util.UpdateChecker;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class GPClaimBlocks extends JavaPlugin {

    private static GPClaimBlocks instance;

    private RankManager rankManager;
    private VaultHook vaultHook;
    private LuckPermsHook luckPermsHook;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        rankManager = new RankManager(this);

        setupHooks();
        setupListeners();
        setupCommands();
        setupMetrics();

        getLogger().info("Loaded " + rankManager.getRankCount() + " ranks.");
    }

    @Override
    public void onDisable() {
        if (luckPermsHook != null) luckPermsHook.unregister();
    }

    private void setupHooks() {
        if (isPluginPresent("Vault")) {
            vaultHook = new VaultHook();
            if (vaultHook.setup()) {
                getLogger().info("Hooked into Vault.");
            } else {
                vaultHook = null;
            }
        }

        if (isPluginPresent("LuckPerms")) {
            luckPermsHook = new LuckPermsHook(this);
            if (luckPermsHook.setup()) {
                getLogger().info("Hooked into LuckPerms.");
            } else {
                luckPermsHook = null;
            }
        }

        if (isPluginPresent("PlaceholderAPI")) {
            new PlaceholderHook(this).register();
            getLogger().info("Hooked into PlaceholderAPI.");
        }
    }

    private void setupListeners() {
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }

    private void setupCommands() {
        CommandHandler handler = new CommandHandler(this);
        getCommand("gpclaimblocks").setExecutor(handler);
        getCommand("gpclaimblocks").setTabCompleter(handler);
    }

    private void setupMetrics() {
        new Metrics(this, 24412);
        new UpdateChecker(this, 130676).check();
    }

    private boolean isPluginPresent(String name) {
        return Bukkit.getPluginManager().getPlugin(name) != null;
    }

    public void reload() {
        reloadConfig();
        rankManager.reload();
        Bukkit.getOnlinePlayers().forEach(rankManager::updateClaimBlocks);
        getLogger().info("Reloaded " + rankManager.getRankCount() + " ranks.");
    }

    public void debug(String msg) {
        if (getConfig().getBoolean("debug")) {
            getLogger().info("[Debug] " + msg);
        }
    }

    public static GPClaimBlocks getInstance() {
        return instance;
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public VaultHook getVaultHook() {
        return vaultHook;
    }
}
