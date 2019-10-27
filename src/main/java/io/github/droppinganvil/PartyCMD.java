package io.github.droppinganvil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyCMD implements CommandExecutor {
    Main plugin;
    public PartyCMD(Main instance) {
        plugin = instance;
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player)sender;
        if (args.length < 1){
            sender.sendMessage(plugin.messages.getString("NotEnoughArguments").replace("&", "§"));
            return true;
        }
        if (args[0].equalsIgnoreCase("create")){
            plugin.createParty(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("invite")){
            if (args.length > 1) {
                Player pL = Bukkit.getPlayer(args[1]);
                if (pL != null) {
                    plugin.inviteToParty(player, pL);
                } else {
                    sender.sendMessage(ChatColor.RED + "Error: Player not found");
                }
            } else {
                sender.sendMessage(plugin.messages.getString("NotEnoughArguments").replace("&", "§"));
                return true;
            }
        }
        if (args.length > 1){
            if (args[0].equalsIgnoreCase("name")){
                plugin.nameParty(player, args[1]);
                sender.sendMessage(plugin.messages.getString("PartyNamed").replace("&", "§"));
            }
        }
        if (args[0].equalsIgnoreCase("join")) {
            if (args.length > 1) {
                plugin.joinParty(player, args[1]);
            } else {
                sender.sendMessage(plugin.messages.getString("NotEnoughArguments").replace("&", "§"));
            }
        }
        if (args[0].equalsIgnoreCase("leave")){
            if(plugin.isPartyLeader(player)){
                plugin.disbandParty(player);
            } else {
                plugin.leaveParty(player);
            }
        }
        return true;
    }
}
