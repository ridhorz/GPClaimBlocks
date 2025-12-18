package dev.idhoorz.gpclaimblocks.hook;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class VaultHook {

    private Permission permission;

    public boolean setup() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (rsp == null) return false;

        permission = rsp.getProvider();
        return permission != null;
    }

    public boolean isInGroup(Player player, String group) {
        if (permission == null) return false;
        try {
            return permission.playerInGroup(player, group);
        } catch (Exception e) {
            return false;
        }
    }
}
