package io.github.droppinganvil;

import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import io.github.droppinganvil.Main;
import org.bukkit.entity.Player;

/**
 * This class will be registered through the register-method in the
 * plugins onEnable-method.
 */
public class Placeholders extends PlaceholderExpansion {

    Main plugin;
    public Placeholders(Main instance) {
        plugin = instance;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier(){
        return "novasurvival";
    }

    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null){
            return "";
        }

        if(identifier.equals("party")){
            if (plugin.isInParty(player)){
                String leader = plugin.getPartyLeaderUUID(player.getUniqueId()).toString();
                return plugin.parties.getString("Parties." + leader + ".Name");
            }
            return plugin.getConfig().getString("PartyUnnamedPlaceholder");

        }

        if(identifier.equals("claimsremaining")){
            return plugin.claimsRemaining(player);
        }
        return null;
    }
}
