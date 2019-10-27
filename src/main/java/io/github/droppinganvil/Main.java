package io.github.droppinganvil;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import static org.bukkit.Bukkit.getPlayer;
//TODO Make claim GUI
public class Main extends JavaPlugin {
    public List<String> getClaims(Player player){
        if (database.getConfigurationSection("ClaimsToPlayers").getKeys(false).contains(player.getUniqueId().toString())){
            return database.getStringList("ClaimsToPlayers." + player.getUniqueId().toString() + ".Claims");
        }
        List<String> x = new ArrayList();
        return x;
    }
    public void saveDatabase(){
        try {
            database.save(data);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    public String claimsRemaining(Player player){
        Set<String> x = database.getConfigurationSection("ClaimsToPlayers").getKeys(false);
        if (!(player.hasPermission("nova.claim") || player.hasPermission("nova.claim.vip") || player.hasPermission("nova.claim.mvp"))){
            return "0";
        } else {
            if (!player.hasPermission("nova.claim.mvp") && player.hasPermission("nova.claim.vip")){ //Check means they are level VIP
                if (x.contains(player.getUniqueId().toString())){
                    Integer c = database.getStringList("ClaimsToPlayers." + player.getUniqueId().toString() + ".Claims").size() * 16;
                    return Integer.toString(getConfig().getInt("MaxBlocksVIP") - c);
                } else return Integer.toString(getConfig().getInt("MaxBlocksVIP"));
            }
            if (player.hasPermission("nova.claim.mvp")){ //Check means they are level MVP
                if (x.contains(player.getUniqueId().toString())){
                    Integer c = database.getStringList("ClaimsToPlayers." + player.getUniqueId().toString() + ".Claims").size() * 16;
                    return Integer.toString(getConfig().getInt("MaxBlocksMVP") - c);
                } else return Integer.toString(getConfig().getInt("MaxBlocksMVP"));
            }
            if (x.contains(player.getUniqueId().toString())){
                Integer c = database.getStringList("ClaimsToPlayers." + player.getUniqueId().toString() + ".Claims").size() * 16;
                return Integer.toString(getConfig().getInt("MaxBlocksReg") - c);
            } else return Integer.toString(getConfig().getInt("MaxBlocksReg"));
        }
    }
//TODO: Change the claim int to our new method above
    public void saveClaim(Chunk chunk, CommandSender sender) {
        if (!(sender.hasPermission("nova.claim"))){
            messages.getString("NoPerms").replace("&", "§");
            return;
        }
        Player player = (Player)sender;
        Set<String> c = database.getConfigurationSection("ClaimsToPlayers").getKeys(false);
        if (sender.hasPermission("nova.claim.vip") && !sender.hasPermission("nova.claim.mvp")){
                if (c.contains(player.getUniqueId().toString())){
                    if (database.getStringList("ClaimsToPlayers." + player.getUniqueId().toString() + ".Claims").size() == getConfig().getInt("MaxBlocksVIP")){
                        sender.sendMessage(messages.getString("MaxClaims").replace("&", "§"));
                        return;
                    }
            }
        }
        if (sender.hasPermission("nova.claim.mvp")){
                if (c.contains(player.getUniqueId().toString())){
                    if (database.getStringList("ClaimsToPlayers." + player.getUniqueId().toString() + ".Claims").size() == getConfig().getInt("MaxBlocksMVP")){
                        sender.sendMessage(messages.getString("MaxClaims").replace("&", "§"));
                        return;
                    }
            }
        }
        Boolean b = false;
            String s = chunkToClaimFormat(chunk);
            if (isLandClaimed(chunk)) {
                sender.sendMessage(messages.getString("AlreadyClaimed").replace("&", "§"));
            } else {
                database.set("Claims." + s + ".Owner", player.getUniqueId().toString());
                List<String> dummyList = new ArrayList();
                database.set("Claims." + s + ".Admins", dummyList);
                database.set("Claims." + s + ".Builders", dummyList);
                database.set("Claims." + s + ".Arena", b);
                sender.sendMessage(messages.getString("JustClaimed").replace("&", "§"));
                if (c.contains(player.getUniqueId().toString())){
                    List<String> x = database.getStringList("ClaimsToPlayers." + player.getUniqueId().toString() + ".Claims");
                    x.add(chunkToClaimFormat(chunk));
                    database.set("ClaimsToPlayers." + player.getUniqueId().toString() + ".Claims", x);
                } else {
                    List<String> x = new ArrayList();
                    x.add(chunkToClaimFormat(chunk));
                    database.set("ClaimsToPlayers." + player.getUniqueId().toString() + ".Claims", x);
                }

        }
        saveDatabase();
    }
//TODO add support for claims to player
    public void deleteClaim(Chunk chunk, CommandSender sender) {
        if (isLandClaimed(chunk)) {
            Player player = (Player) sender;
            String s = chunkToClaimFormat(chunk);
            if (!(database.getString("Claims." + s + ".Owner").equals(player.getUniqueId().toString())))
                sender.sendMessage(messages.getString("NotClaimOwner").replace("&", "§"));
            else {
                database.set("Claims." + s, null);
                Set<String> c = database.getConfigurationSection("ClaimsToPlayers").getKeys(false);
                    List<String> x = database.getStringList("ClaimsToPlayers." + player.getUniqueId().toString() + ".Claims");
                    x.remove(chunkToClaimFormat(chunk));
                    database.set("ClaimsToPlayers." + player.getUniqueId().toString() + ".Claims", x);
                sender.sendMessage(messages.getString("ClaimDeleted").replace("&", "§"));
            }

        } else {
            sender.sendMessage(messages.getString("NotClaimed").replace("&", "§"));
        }
        saveDatabase();
    }
public boolean isClaimBuildAuthorized(Chunk chunk, Player player){
        String s = chunkToClaimFormat(chunk);
        if (database.getString("Claims." + s + ".Owner").equals(player.getUniqueId().toString())){
            return true;
        } else {
            if (database.getString("Claims." + s + ".Builders").contains(player.getUniqueId().toString())){
                return true;
            } else return false;
        }
}
public void addBuilder(Chunk chunk, Player player, String playerName){
        String s = chunkToClaimFormat(chunk);
        if (database.getString("Claims." + s + ".Owner").equals(player.getUniqueId().toString())) {
            List<String> x = database.getStringList("Claims." + s + ".Builders");
            Player target = Bukkit.getPlayer(playerName);
            if (target != null){
                x.add(target.getUniqueId().toString());
                database.set("Claims." + s + ".Builders", x);
                player.sendMessage(messages.getString("BuilderAdded").replace("&", "§"));
                target.sendMessage(messages.getString("NowBuilder").replace("&", "§").replace("%Player%", player.getName()).replace("%Claim%", s));
            }
        } else player.sendMessage(messages.getString("NotClaimOwner").replace("&", "§"));
    saveDatabase();
    }
    public void removeBuilder(Chunk chunk, Player player, String playerName){
        String s = chunkToClaimFormat(chunk);
        if (database.getString("Claims." + s + ".Owner").equals(player.getUniqueId().toString())) {
            List<String> x = database.getStringList("Claims." + s + ".Builders");
            Player target = Bukkit.getPlayer(playerName);
            if (target != null){
                x.remove(target.getUniqueId().toString());
                database.set("Claims." + s + ".Builders", x);
                player.sendMessage(messages.getString("BuilderRemoved").replace("&", "§"));
                target.sendMessage(messages.getString("NowNotBuilder").replace("&", "§").replace("%Player%", player.getName()).replace("%Claim%", s));
            }
        } else player.sendMessage(messages.getString("NotClaimOwner").replace("&", "§"));
        saveDatabase();
    }
    public void saveChest(Location loc, Player player) {
        for (String claims : database.getConfigurationSection("LockedChest").getKeys(false)) {
            StringBuilder s = new StringBuilder();
            s.append(loc.getBlockX());
            s.append(",");
            s.append(loc.getBlockZ());
            s.append(",");
            s.append(loc.getBlockY());
            s.append(",");
            s.append(loc.getWorld().getName());
            if (claims.contains(s.toString())) {
                player.sendMessage(messages.getString("ChestAlreadyClaimed").replace("&", "§"));
            } else {
                database.set("LockedChest." + s + "Owner", player.getUniqueId().toString());
            }

        }
        saveDatabase();
    }
    public boolean isClaimOwner(Chunk chunk, Player player){
        if (database.getString("Claims." + chunkToClaimFormat(chunk) + ".Owner").equals(player.getUniqueId().toString())){
            return true;
        } else return false;
    }

    public boolean isArena(Chunk chunk){
        String s = chunkToClaimFormat(chunk);
        if (isLandClaimed(chunk)){
            if (database.getBoolean("Claims." + s + ".Arena")){
                return true;
            }
        }
        return false;
    }

    public void createParty(Player player) {
        List<String> inParties = parties.getStringList("InParties");
        if (!(parties.getConfigurationSection("Parties").getKeys(false).isEmpty())) {
            if (parties.getStringList("InParties").contains(player.getUniqueId().toString())) {
                player.sendMessage(messages.getString("AlreadyInParty").replace("&", "§"));
            } else {
                List<String> dummyList = new ArrayList();
                List<String> playerList = new ArrayList();
                playerList.add(player.getUniqueId().toString());
                parties.set("Parties." + player.getUniqueId().toString() + ".Players", playerList);
                playerList.remove(player.getUniqueId().toString());
                parties.set("Parties." + player.getUniqueId().toString() + ".Invites", dummyList);
                player.sendMessage(messages.getString("PartyCreated").replace("&", "§"));
                inParties.add(player.getUniqueId().toString());
                parties.set("InParties", inParties);
                parties.set("Parties." + player.getUniqueId().toString() + ".Name", "");
            }
        } else {
            List<String> dummyList = new ArrayList();
            List<String> playerList = new ArrayList();
            playerList.add(player.getUniqueId().toString());
            parties.set("Parties." + player.getUniqueId().toString() + ".Players", playerList);
            parties.set("Parties." + player.getUniqueId().toString() + ".Invites", dummyList);
            player.sendMessage(messages.getString("PartyCreated").replace("&", "§"));
            inParties.add(player.getUniqueId().toString());
            parties.set("InParties", inParties);
            parties.set("Parties." + player.getUniqueId().toString() + ".Name", "");
        }
        saveParties();
    }
    public void nameParty(Player player, String name){
        String s = player.getUniqueId().toString();
        parties.set("Parties." + s + ".Name", name);
        saveParties();
    }
    public void joinParty(Player player, String playerName) {
        if (parties.getStringList("InParties").contains(player.getUniqueId().toString())) {
            player.sendMessage(messages.getString("AlreadyInParty").replace("&", "§"));
        } else {
            Player target = Bukkit.getPlayer(playerName);
            if (target != null && parties.getConfigurationSection("Parties").getKeys(false).contains(target.getUniqueId().toString())) {
                List<String> invites = parties.getStringList("Parties." + target.getUniqueId().toString() + ".Invites");
                if (invites.contains(player.getUniqueId().toString())) {
                    List<String> players = parties.getStringList("Parties." + target.getUniqueId().toString() + ".Players");
                    if (players.size() == getConfig().getInt("MaxPartySize")){
                        player.sendMessage(messages.getString("MaxPlayersInParty").replace("&", "§"));
                        return;
                    }
                    invites.remove(player.getUniqueId().toString());
                    players.add(player.getUniqueId().toString());
                    parties.set("Parties." + target.getUniqueId().toString() + ".Players", players);
                    player.sendMessage(messages.getString("PartyJoined").replace("&", "§"));
                    for (String p : players) {
                        Player pL = Bukkit.getPlayer(p);
                        if (pL != null) {
                            pL.sendMessage(messages.getString("PlayerJoinedYourParty").replace("&", "§").replace("%Player%", player.getName()));
                        }
                    }
                    parties.set("Parties." + target.getUniqueId().toString() + ".Invites", invites);
                    List<String> inParties = parties.getStringList("InParties");
                    inParties.add(player.getUniqueId().toString());
                    parties.set("InParties", inParties);
                } else {
                    player.sendMessage(messages.getString("AttemptedJoinWithoutInvite").replace("&", "§"));
                }
            } else {
                player.sendMessage(ChatColor.RED + "Error: Cannot find that players party!");
            }
        }
        saveParties();
    }
//TODO: Fix invite and join MSGs
    public void inviteToParty(Player player, Player targetPlayer) {
        if (isPartyLeader(player)) {
            List<String> x = parties.getStringList("Parties." + player.getUniqueId().toString() + ".Invites");
            List<String> p = parties.getStringList("Parties." + player.getUniqueId().toString() + ".Players");
            x.add(targetPlayer.getUniqueId().toString());
            parties.set("Parties." + player.getUniqueId().toString() + ".Invites", x);
            player.sendMessage(messages.getString("PlayerInvited").replace("&", "§").replace("%Player%", targetPlayer.getName()));
            for (String pNO : p) {
                Player pL = Bukkit.getPlayer(pNO);
                if (pL != null) {
                    pL.sendMessage(messages.getString("PlayerInvitedToParty").replace("&", "§").replace("%Player%", player.getName()).replace("%TargetPlayer%", targetPlayer.getName()));
                }
            }
        } else {
            if (isInParty(player))
            player.sendMessage(messages.getString("NotInParty").replace("&", "§"));
            else player.sendMessage(messages.getString("NotPartyLeader").replace("&", "§"));
        }
        saveParties();
    }

    public void disbandParty(Player player) {
        parties.set("Parties." + player.getUniqueId().toString(), null);
        for (String pl : parties.getStringList("Parties." + player.getUniqueId().toString() + ".Players")) {
            Player pL = Bukkit.getPlayer(pl);
            List<String> inParties = parties.getStringList("InParties");
            inParties.remove(pl);
            parties.set("InParties", inParties);
            player.sendMessage(messages.getString("PartyDisbanded").replace("&", "§"));
            if (pL != null) {
                pL.sendMessage(messages.getString("PlayerDisbandedYourParty").replace("&", "§").replace("%Player%", player.getName()));
            }
        }
saveParties();
    }

    public boolean isPartyLeader(Player player) {
        Set<String> x = parties.getConfigurationSection("Parties").getKeys(false);
        if (x.contains(player.getUniqueId().toString())) {
            return true;
        } else return false;
    }

    public boolean isInParty(Player player) {
        if (parties.getStringList("InParties").contains(player.getUniqueId().toString())) {
            return true;
        } else {
            return false;
        }
    }

    public String chunkToClaimFormat(Chunk chunk) {
        StringBuilder s = new StringBuilder();
        s.append(chunk.getX());
        s.append(",");
        s.append(chunk.getZ());
        s.append(",");
        s.append(chunk.getWorld().getName());
        return s.toString();
    }

    public boolean isLandClaimed(Chunk chunk) {
        if (database.getConfigurationSection("Claims").getKeys(false).contains(chunkToClaimFormat(chunk))) {
            return true;
        } else {
            return false;
        }
    }

    //Should only be used after checking if they are in a party!
    public UUID getPartyLeaderUUID(UUID player) {
        for (String allParties : parties.getConfigurationSection("Parties").getKeys(false)) {
            if (parties.getStringList("Parties." + allParties + ".Players").contains(player.toString())) {
                return UUID.fromString(allParties);
            }
        }
        return null;
    }
    //Using getPartyLeaderUUID for claims as we cant verify the other player is online
    public boolean isInSameParty(Player player1, Player player2){
        if (isInParty(player1) && isInParty(player2)){
            if (getPartyLeaderUUID(player1.getUniqueId()).equals(getPartyLeaderUUID(player2.getUniqueId()))){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
        public void leaveParty(Player player){
            if (!(parties.getStringList("InParties").contains(player.getUniqueId().toString()))) {
                player.sendMessage(messages.getString("NotInParty").replace("&", "§"));
            } else {
                for (String rP : parties.getConfigurationSection("Parties").getKeys(false)){
                        if (parties.getStringList("Parties." + rP + ".Players").contains(player.getUniqueId().toString())){
                            List<String> x = parties.getStringList("Parties." + rP + ".Players");
                            x.remove(player.getUniqueId().toString());
                            parties.set("Parties." + rP + ".Players", x);
                            parties.getStringList("InParties").remove(player.getUniqueId().toString());
                            for (String p : x){
                                Player pL = Bukkit.getPlayer(p);
                                if (pL != null){
                                    pL.sendMessage(messages.getString("PlayerLeftYourParty").replace("&", "§").replace("%Player%", player.getName()));
                                }
                            }
                        }
                }

            }
            saveParties();
        }
    public void loadAll(){
        config = YamlConfiguration.loadConfiguration(configFile);
        database = YamlConfiguration.loadConfiguration(data);
        messages = YamlConfiguration.loadConfiguration(MSG);
        parties = YamlConfiguration.loadConfiguration(partiesFile);
    }
    public HashSet<String> x;
    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    File configFile;
    FileConfiguration config;
    File data;
    FileConfiguration database;
    File MSG;
    FileConfiguration messages;
    File partiesFile;
    FileConfiguration parties;
    private Main plugin;
    public void saveParties(){
        try {
            parties.save(partiesFile);
        } catch (IOException exception){
            exception.printStackTrace();
        }
    }

    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BlockListeners(this), this);
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new Placeholders(this).register();
            System.out.print("[NovaSurvival] Placeholders registered!");
        } else System.out.print("[NovaSurvival] PlaceholderAPI not found!");
        configFile = new File(getDataFolder(), "Config.yml");
        data = new File(getDataFolder(), "Database.yml");
        database = YamlConfiguration.loadConfiguration(data);
        MSG = new File(getDataFolder(), "Messages.yml");
        messages = YamlConfiguration.loadConfiguration(MSG);
        partiesFile = new File(getDataFolder(), "Parties.yml");
        parties = YamlConfiguration.loadConfiguration(partiesFile);
        x = new HashSet();
        System.out.print("########### NovaSurvival ###########");
        getCommand("claim").setExecutor(new ClaimCMD(this));
        getCommand("party").setExecutor(new PartyCMD(this));
        getCommand("unclaim").setExecutor(new UnclaimCMD(this));
        getCommand("s").setExecutor(new S(this));
        getCommand("claims").setExecutor(new CGUI(this));
        if (!data.exists()) {
            try {
                this.saveResource("Database.yml", false);
                this.saveResource("Messages.yml", false);
                this.saveResource("Config.yml", false);
                database.createSection("Claims");
                database.createSection("LockedChest");
                database.createSection("ClaimsToPlayers");
                parties.createSection("Parties");
                List<String> dummyList = new ArrayList();
                parties.createSection("InParties");
                parties.set("InParties", dummyList);
                parties.save(partiesFile);
                database.save(data);
                loadAll();
            } catch (IOException exception) {

                exception.printStackTrace();
            }
        }
    }
    public void onDisable(){
        try {
            parties.save(partiesFile);
        } catch (IOException exception){
            exception.printStackTrace();
        }
    }
}
