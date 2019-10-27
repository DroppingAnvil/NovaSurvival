package io.github.droppinganvil;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClaimCMD implements CommandExecutor {
    Main plugin;
    public ClaimCMD(Main instance) {
        plugin = instance;
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player)sender;
        if (args.length < 1) {
            plugin.saveClaim(player.getLocation().getChunk(), sender);
            return true;
        }
        if (plugin.isClaimOwner(player.getLocation().getChunk(), player)){
        if (!(args.length < 2)) {
            if (args[0].equalsIgnoreCase("arena") && (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false"))) {
                String s = plugin.chunkToClaimFormat(player.getLocation().getChunk());
                if (args[1].equalsIgnoreCase("true")) {
                    Boolean b = true;
                    plugin.database.set("Claims." + s + ".Arena", b);
                    sender.sendMessage(plugin.messages.getString("Arena.ArenaValueChanged").replace("&", "§"));
                    return true;
                } else {
                    Boolean b = false;
                    plugin.database.set("Claims." + s + ".Arena", b);
                    sender.sendMessage(plugin.messages.getString("Arena.ArenaValueChanged").replace("&", "§"));
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("remove") && args[1].equalsIgnoreCase("builder")) {
                plugin.removeBuilder(player.getLocation().getChunk(), player, args[2]);
                return true;
            }
            if (args[0].equalsIgnoreCase("add") && args[1].equalsIgnoreCase("builder")) {
                plugin.addBuilder(player.getLocation().getChunk(), player, args[2]);
                return true;
            }
        }
        } else {
            player.sendMessage(plugin.messages.getString("NotClaimOwner").replace("&", "§"));
        }
            player.sendMessage(plugin.messages.getString("IncorrectUsage").replace("&", "§"));
        return true;
    }
}
