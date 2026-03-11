package com.prisoncory.managers;

import com.prisoncory.PrisonCoreY;
import com.prisoncory.drugs.DrugData;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlantLimitManager {
    
    private final PrisonCoreY plugin;
    public final Map<UUID, DrugData> playerData;
    private final File dataFile;
    private FileConfiguration dataConfig;
    
    public PlantLimitManager(PrisonCoreY plugin) {
        this.plugin = plugin;
        this.playerData = new HashMap<>();
        this.dataFile = new File(plugin.getDataFolder(), "drugdata.yml");
        loadData();
    }
    
    private void loadData() {
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create drugdata.yml!");
                e.printStackTrace();
            }
        }
        
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        
        for (String uuidString : dataConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                Set<String> locations = dataConfig.getStringList(uuidString).stream().collect(Collectors.toSet());
                DrugData data = new DrugData(uuid, locations);
                playerData.put(uuid, data);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load data for UUID: " + uuidString);
            }
        }
    }
    
    public void saveAllData() {
        dataConfig = new YamlConfiguration();
        
        for (Map.Entry<UUID, DrugData> entry : playerData.entrySet()) {
            dataConfig.set(entry.getKey().toString(), entry.getValue().getPlantLocations().stream().collect(Collectors.toList()));
        }
        
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save drugdata.yml!");
            e.printStackTrace();
        }
    }
    
    public DrugData getPlayerData(UUID playerId) {
        return playerData.computeIfAbsent(playerId, uuid -> new DrugData(uuid, new java.util.HashSet<>()));
    }
    
    public void addPlant(UUID playerId, Location location) {
        DrugData data = getPlayerData(playerId);
        data.addPlant(location);
    }
    
    public void removePlant(UUID playerId, Location location) {
        DrugData data = getPlayerData(playerId);
        data.removePlant(location);
    }
    
    public int getCurrentPlants(UUID playerId) {
        return getPlayerData(playerId).getPlantCount();
    }
    
    public int getMaxPlants(Player player) {
        String rank = getPlayerRank(player);
        return plugin.getConfigManager().getDrugLimit(rank);
    }
    
    public boolean canPlant(Player player) {
        return getCurrentPlants(player.getUniqueId()) < getMaxPlants(player);
    }
    
    public String getPlayerRank(Player player) {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") == null) {
            return "default";
        }
        
        try {
            LuckPerms luckPerms = LuckPermsProvider.get();
            User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            
            if (user == null) {
                return "default";
            }
            
            Set<String> groups = user.getNodes().stream()
                    .filter(node -> node instanceof InheritanceNode)
                    .map(node -> ((InheritanceNode) node).getGroupName())
                    .collect(Collectors.toSet());
            
            for (String group : new String[]{"elite", "mvp", "vip"}) {
                if (groups.contains(group)) {
                    return group;
                }
            }
            
            return "default";
        } catch (Exception e) {
            return "default";
        }
    }
}