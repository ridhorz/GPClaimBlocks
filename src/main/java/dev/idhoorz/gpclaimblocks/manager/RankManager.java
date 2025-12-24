package dev.idhoorz.gpclaimblocks.manager;

import dev.idhoorz.gpclaimblocks.GPClaimBlocks;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RankManager {

    private final GPClaimBlocks plugin;
    private final Map<String, Integer> ranks = new LinkedHashMap<>();

    private int defaultBlocks;
    private boolean accumulative;
    private boolean useVaultGroups;

    public RankManager(GPClaimBlocks plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        ranks.clear();

        defaultBlocks = Math.max(0, plugin.getConfig().getInt("default-bonus-blocks", 400));
        accumulative = plugin.getConfig().getBoolean("accumulative", false);
        useVaultGroups = plugin.getConfig().getBoolean("use-vault-groups", false);

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("ranks");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ranks.put(key, Math.max(0, section.getInt(key)));
            }
        }
    }

    public void updateClaimBlocks(Player player) {
        if (GriefPrevention.instance == null) return;

        PlayerData data = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        if (data == null) return;

        int blocks = accumulative ? calcAccumulative(player) : getRank(player).blocks;
        data.setBonusClaimBlocks(blocks);

        plugin.debug(player.getName() + " -> " + blocks + " blocks");
    }

    public RankInfo getRank(Player player) {
        for (Map.Entry<String, Integer> entry : ranks.entrySet()) {
            if (hasRank(player, entry.getKey())) {
                return new RankInfo(entry.getKey(), entry.getValue());
            }
        }
        return new RankInfo(null, defaultBlocks);
    }

    private int calcAccumulative(Player player) {
        int total = defaultBlocks;
        for (Map.Entry<String, Integer> entry : ranks.entrySet()) {
            if (hasRank(player, entry.getKey())) {
                total += entry.getValue();
            }
        }
        return total;
    }

    private boolean hasRank(Player player, String key) {
        // Check exact permission first
        if (player.hasPermission(key)) return true;

        // Auto-detect: if key doesn't contain dot, try with "group." prefix
        if (!key.contains(".") && player.hasPermission("group." + key)) return true;

        // Vault fallback
        if (useVaultGroups && plugin.getVaultHook() != null) {
            return plugin.getVaultHook().isInGroup(player, extractName(key));
        }

        return false;
    }

    private String extractName(String key) {
        int dot = key.lastIndexOf('.');
        return dot >= 0 ? key.substring(dot + 1) : key;
    }

    public int getRankCount() {
        return ranks.size();
    }

    public int getDefaultBlocks() {
        return defaultBlocks;
    }

    public Map<String, Integer> getRanks() {
        return Collections.unmodifiableMap(ranks);
    }

    public static final class RankInfo {
        public final String permission;
        public final int blocks;

        RankInfo(String permission, int blocks) {
            this.permission = permission;
            this.blocks = blocks;
        }

        public String getName() {
            if (permission == null) return "default";
            int dot = permission.lastIndexOf('.');
            return dot >= 0 ? permission.substring(dot + 1) : permission;
        }
    }
}
