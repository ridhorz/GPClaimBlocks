package dev.idhoorz.gpclaimblocks.command;

import dev.idhoorz.gpclaimblocks.GPClaimBlocks;
import dev.idhoorz.gpclaimblocks.manager.RankManager.RankInfo;
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

public final class CommandHandler implements CommandExecutor, TabCompleter {

    private static final List<String> COMMANDS = Arrays.asList("reload", "check", "list", "update");

    private final GPClaimBlocks plugin;

    public CommandHandler(GPClaimBlocks plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("gpclaimblocks.admin")) {
            send(sender, "messages.no-permission");
            return true;
        }

        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reload();
                send(sender, "messages.reload-success");
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
                showHelp(sender);
        }
        return true;
    }

    private void checkPlayer(CommandSender sender, String name) {
        Player target = Bukkit.getPlayer(name);
        if (target == null) {
            send(sender, "messages.player-not-found");
            return;
        }

        RankInfo rank = plugin.getRankManager().getRank(target);
        int blocks = GriefPrevention.instance.dataStore.getPlayerData(target.getUniqueId()).getBonusClaimBlocks();

        String msg = plugin.getConfig().getString("messages.check-result", "")
                .replace("%player%", target.getName())
                .replace("%blocks%", String.valueOf(blocks))
                .replace("%rank%", rank.getName());

        sender.sendMessage(color(msg));
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
            return;
        }

        Player target = Bukkit.getPlayer(name);
        if (target == null) {
            send(sender, "messages.player-not-found");
            return;
        }

        plugin.getRankManager().updateClaimBlocks(target);
        sender.sendMessage(ChatColor.GREEN + "Updated " + target.getName());
    }

    private void showHelp(CommandSender sender) {
        String ver = plugin.getDescription().getVersion();
        sender.sendMessage(ChatColor.GOLD + "=== GPClaimBlocks v" + ver + " ===");
        sender.sendMessage(ChatColor.YELLOW + "/gpcb reload " + ChatColor.GRAY + "- Reload config");
        sender.sendMessage(ChatColor.YELLOW + "/gpcb check <player> " + ChatColor.GRAY + "- Check player");
        sender.sendMessage(ChatColor.YELLOW + "/gpcb list " + ChatColor.GRAY + "- List ranks");
        sender.sendMessage(ChatColor.YELLOW + "/gpcb update [player] " + ChatColor.GRAY + "- Force update");
    }

    private void send(CommandSender sender, String path) {
        String msg = plugin.getConfig().getString(path);
        if (msg != null) sender.sendMessage(color(msg));
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if (!sender.hasPermission("gpclaimblocks.admin")) return result;

        if (args.length == 1) {
            String input = args[0].toLowerCase();
            for (String c : COMMANDS) {
                if (c.startsWith(input)) result.add(c);
            }
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("check") || args[0].equalsIgnoreCase("update"))) {
            String input = args[1].toLowerCase();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(input)) result.add(p.getName());
            }
        }
        return result;
    }
}
