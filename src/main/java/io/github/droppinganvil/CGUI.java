package io.github.droppinganvil;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CGUI implements CommandExecutor {
    Main plugin;

    public CGUI(Main instance) {
        plugin = instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        Integer x = 0;
        Inventory i = Bukkit.createInventory(player, 54, plugin.getConfig().getString("ClaimGui.Name").replace("&", "§"));
        for (String claim : plugin.getClaims(player)) {
            ItemStack iS = new ItemStack(Enum.valueOf(Material.class, plugin.getConfig().getString("ClaimGui.Material")));
            ItemMeta m = iS.getItemMeta();
            if (plugin.getConfig().getBoolean("ClaimGui.UseLore")) {
                List<String> lore = new ArrayList();
                lore.add(plugin.getConfig().getString("ClaimGui.Lore").replace("%Claim%", claim).replace("&", "§"));
                m.setLore(lore);
            }
            m.setDisplayName(plugin.getConfig().getString("ClaimGui.MaterialName").replace("&", "§").replace("%Claim%", claim));
            iS.setItemMeta(m);
            i.addItem(iS);
        }
        plugin.x.add(player.getName());
        player.openInventory(i);
        return true;
    }
}
