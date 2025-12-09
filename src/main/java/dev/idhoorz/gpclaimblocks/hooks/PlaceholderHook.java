package dev.idhoorz.gpclaimblocks.hooks;

import dev.idhoorz.gpclaimblocks.GPClaimBlocks;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PlaceholderHook extends PlaceholderExpansion {

    private final GPClaimBlocks plugin;

    public PlaceholderHook(GPClaimBlocks plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "gpcb";
    }

    @Override
    public @NotNull String getAuthor() {
        return "IdhooRZ";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";

        PlayerData data = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        if (data == null) return "0";

        switch (params.toLowerCase()) {
            case "bonus": return String.valueOf(data.getBonusClaimBlocks());
            case "accrued": return String.valueOf(data.getAccruedClaimBlocks());
            case "total": return String.valueOf(data.getBonusClaimBlocks() + data.getAccruedClaimBlocks());
            case "remaining": return String.valueOf(data.getRemainingClaimBlocks());
            case "rank": return plugin.getRankManager().getRank(player).getName();
            default: return null;
        }
    }
}
