package dev.idhoorz.gpclaimblocks.commands;

import dev.idhoorz.gpclaimblocks.GPClaimBlocks;
import dev.idhoorz.gpclaimblocks.managers.RankManager.RankInfo;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class MainCommand implements CommandExecutor, TabCompleter {

    private final GPClaimBlocks plugin;

    public MainCommand(GPClaimBlocks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("gpclaimblocks.admin")) {
            sender.sendMessage(color(plugin.getConfig().getString("messages.no-permission")));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reload();
                sender.sendMessage(color(plugin.getConfig().getString("messages.reload-success")));
                break;
            case "check":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " check <player>");
                } else {
                    checkPlayer(sender, args[1]);
                }
                break;
            case "list":
                listRanks(sender);
                break;
            case "update":
                updatePlayers(sender, args.length > 1 ? args[1] : null);
                break;
            default:
                sendHelp(sender);
        }
        return true;
    }

    private void checkPlayer(CommandSender sender, String name) {
        Player target = Bukkit.getPlayer(name);
        if (target == null) {
            sender.sendMessage(color(plugin.getConfig().getString("messages.player-not-found")));
            return;
        }

        RankInfo rank = plugin.getRankManager().getRank(target);
        int blocks = GriefPrevention.instance.dataStore.getPlayerData(target.getUniqueId()).getBonusClaimBlocks();

        sender.sendMessage(color(plugin.getConfig().getString("messages.check-result")
                .replace("%player%", target.getName())
                .replace("%blocks%", String.valueOf(blocks))
                .replace("%rank%", rank.getName())));
    }

    private void listRanks(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== GPClaimBlocks Ranks ===");
        sender.sendMessage(ChatColor.GRAY + "Default: " + ChatColor.WHITE + plugin.getRankManager().getDefaultBlocks());

        for (Map.Entry<String, Integer> e : plugin.getRankManager().getRanks().entrySet()) {
            sender.sendMessage(ChatColor.YELLOW + e.getKey() + ChatColor.GRAY + " -> " + ChatColor.GREEN + e.getValue());
        }
    }

    private void updatePlayers(CommandSender sender, String name) {
        if (name == null) {
            Bukkit.getOnlinePlayers().forEach(p -> plugin.getRankManager().updateClaimBlocks(p));
            sender.sendMessage(ChatColor.GREEN + "Updated all online players.");
        } else {
            Player target = Bukkit.getPlayer(name);
            if (target == null) {
                sender.sendMessage(color(plugin.getConfig().getString("messages.player-not-found")));
                return;
            }
            plugin.getRankManager().updateClaimBlocks(target);
            sender.sendMessage(ChatColor.GREEN + "Updated " + target.getName());
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== GPClaimBlocks v" + plugin.getDescription().getVersion() + " ===");
        sender.sendMessage(ChatColor.YELLOW + "/gpcb reload " + ChatColor.GRAY + "- Reload config");
        sender.sendMessage(ChatColor.YELLOW + "/gpcb check <player> " + ChatColor.GRAY + "- Check player");
        sender.sendMessage(ChatColor.YELLOW + "/gpcb list " + ChatColor.GRAY + "- List ranks");
        sender.sendMessage(ChatColor.YELLOW + "/gpcb update [player] " + ChatColor.GRAY + "- Force update");
    }

    private String color(String s) {
        return s == null ? "" : ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!sender.hasPermission("gpclaimblocks.admin")) return new ArrayList<>();

        if (args.length == 1) {
            return filter(Arrays.asList("reload", "check", "list", "update"), args[0]);
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("update"))) {
            List<String> names = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(p -> names.add(p.getName()));
            return filter(names, args[1]);
        }
        return new ArrayList<>();
    }

    private List<String> filter(List<String> list, String prefix) {
        List<String> result = new ArrayList<>();
        for (String s : list) {
            if (s.toLowerCase().startsWith(prefix.toLowerCase())) result.add(s);
        }
        return result;
    }
}
