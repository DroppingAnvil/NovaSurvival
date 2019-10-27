package io.github.droppinganvil;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnclaimCMD implements CommandExecutor {
    Main plugin;
    public UnclaimCMD(Main instance) {
        plugin = instance;
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player)sender;
        if (args.length < 1) {
            plugin.deleteClaim(player.getLocation().getChunk(), sender);
            return true;
        }
        sender.sendMessage(plugin.messages.getString("IncorrectUsage").replace("&", "§"));
        return true;
    }
}
