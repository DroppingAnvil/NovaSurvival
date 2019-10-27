package io.github.droppinganvil;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Random;
import java.util.UUID;

public class BlockListeners implements Listener {
Main plugin;
    public BlockListeners(Main instance) {
        plugin = instance;
    }
    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        Chunk chunk = event.getBlock().getChunk();
        if (plugin.isLandClaimed(chunk)){
            String claimOwner = plugin.database.getString("Claims." + plugin.chunkToClaimFormat(chunk) + ".Owner");
            //Check based on claim system
            if (claimOwner.contains(event.getPlayer().getUniqueId().toString()) || plugin.database.getStringList("Claims." + plugin.chunkToClaimFormat(chunk) + ".Admins").contains(event.getPlayer().getUniqueId().toString()) || plugin.database.getString("Claims." + plugin.chunkToClaimFormat(chunk) + ".Builders").contains(event.getPlayer().getUniqueId().toString())){
                event.setCancelled(false);
                return;
            } else {
                //Check based on party system
                if (plugin.isInParty(event.getPlayer())){
                    if (!(plugin.getPartyLeaderUUID(event.getPlayer().getUniqueId()).equals(plugin.getPartyLeaderUUID(UUID.fromString(claimOwner))))){
                        event.getPlayer().sendMessage(plugin.messages.getString("CannotEditThisClaim").replace("&", "§"));
                    } else {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(plugin.messages.getString("CannotEditThisClaim").replace("&", "§"));
                    }
                } else {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(plugin.messages.getString("CannotEditThisClaim").replace("&", "§"));
                }
            }
        }
    }
    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Chunk chunk = event.getBlock().getChunk();
        if (plugin.isLandClaimed(chunk)){
            String claimOwner = plugin.database.getString(plugin.chunkToClaimFormat(chunk) + ".Owner");
            //Check based on claim system
            if (plugin.database.getString("Claims." + plugin.chunkToClaimFormat(chunk) + ".Owner").contains(event.getPlayer().getUniqueId().toString()) || plugin.database.getStringList("Claims." + plugin.chunkToClaimFormat(chunk) + ".Admins").contains(event.getPlayer().getUniqueId().toString()) || plugin.database.getString("Claims." + plugin.chunkToClaimFormat(chunk) + ".Builders").contains(event.getPlayer().getUniqueId().toString())){
                event.setCancelled(false);
            } else {
                //Check based on party system
                if (plugin.isInParty(event.getPlayer())){
                    if (!(plugin.getPartyLeaderUUID(event.getPlayer().getUniqueId()).equals(plugin.getPartyLeaderUUID(UUID.fromString(claimOwner))))){
                        event.getPlayer().sendMessage(plugin.messages.getString("CannotEditThisClaim").replace("&", "§"));
                    } else {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(plugin.messages.getString("CannotEditThisClaim").replace("&", "§"));
                    }
                } else {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(plugin.messages.getString("CannotEditThisClaim").replace("&", "§"));
                }
            }
        }
    }
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event){
        if (plugin.getConfig().getBoolean("Explosions.AllExplosionsOff")){
            event.setCancelled(true);
        }
        if (plugin.getConfig().getBoolean("Explosions.NoBlockDamageExplosions")){
            event.blockList().clear();
        }
    }
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
                Player damager = (Player) event.getDamager();
                Player player = (Player) event.getEntity();
                if (plugin.isArena(player.getLocation().getChunk())){
                    event.setCancelled(false);
                    return;
                }
                if (plugin.getConfig().getBoolean("AllowPartyMembersPVP")) {
                    if (plugin.isInParty(player) && plugin.isInParty(damager)) {
                        if (!(plugin.getPartyLeaderUUID(player.getUniqueId()).equals(plugin.getPartyLeaderUUID(damager.getUniqueId())))) {
                            damager.sendMessage(plugin.messages.getString("CannotHurtThisPlayer").replace("&", "§"));
                            event.setCancelled(true);
                        } else {
                            event.setCancelled(false);
                        }
                    } else {
                        event.setCancelled(true);
                        damager.sendMessage(plugin.messages.getString("CannotHurtThisPlayer").replace("&", "§"));
                    }
                } else {
                    event.setCancelled(true);
                    damager.sendMessage(plugin.messages.getString("CannotHurtThisPlayer").replace("&", "§"));
                }
            }
        }
    }
    @EventHandler
    public void onFire(BlockIgniteEvent event){
        if (!(plugin.getConfig().getBoolean("FireEnabled"))){
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onMove(PlayerMoveEvent event){
        if (!plugin.isArena(event.getFrom().getChunk()) && plugin.isArena(event.getTo().getChunk())){
            event.getPlayer().sendTitle(plugin.messages.getString("Arena.Text1").replace("&", "§"), plugin.messages.getString("Arena.Text2").replace("&", "§"));
            event.getPlayer().sendMessage(plugin.messages.getString("Arena.EnteringChatMSG").replace("&", "§"));
            return;
        }
        if (!plugin.isLandClaimed(event.getFrom().getChunk()) && plugin.isLandClaimed(event.getTo().getChunk())){
            String ownerE = Bukkit.getOfflinePlayer(UUID.fromString(plugin.database.getString("Claims." + plugin.chunkToClaimFormat(event.getTo().getChunk()) + ".Owner"))).getName();
            event.getPlayer().sendTitle(plugin.messages.getString("Claims.Text1").replace("&", "§").replace("%Player%", ownerE), plugin.messages.getString("Claims.Text2").replace("&", "§").replace("%Player%", ownerE));
            event.getPlayer().sendMessage(plugin.messages.getString("Claims.EnteringChatMSG").replace("&", "§").replace("%Player%", ownerE));
        }
        if (plugin.isLandClaimed(event.getFrom().getChunk()) && !plugin.isLandClaimed(event.getTo().getChunk())){
            String ownerL = Bukkit.getOfflinePlayer(UUID.fromString(plugin.database.getString("Claims." + plugin.chunkToClaimFormat(event.getFrom().getChunk()) + ".Owner"))).getName();
            event.getPlayer().sendTitle(plugin.messages.getString("Claims.LeavingText1").replace("&", "§").replace("%Player%", ownerL), plugin.messages.getString("Claims.LeavingText2").replace("&", "§").replace("%Player%", ownerL));
            event.getPlayer().sendMessage(plugin.messages.getString("Claims.LeavingChatMSG").replace("&", "§").replace("%Player%", ownerL));
        }
    }
    @EventHandler
    public void onClose(InventoryCloseEvent event){
        if (plugin.x.contains(event.getPlayer().getName())){
            plugin.x.remove(event.getPlayer().getName());
        }
    }
    @EventHandler
    public void onClick(InventoryClickEvent event){
        if (plugin.x.contains(event.getWhoClicked().getName())){
            event.setCancelled(true);
        }
    }
}
