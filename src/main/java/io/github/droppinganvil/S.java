package io.github.droppinganvil;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class S implements CommandExecutor {
    Main plugin;
    public S(Main instance) {
        plugin = instance;
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        for (String s : plugin.messages.getStringList("Help")){
            sender.sendMessage(s.replace("&", "§"));
        }
        return true;
    }
}
