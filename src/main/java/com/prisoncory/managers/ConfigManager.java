package com.prisoncory.managers;

import com.prisoncory.PrisonCoreY;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    
    private final PrisonCoreY plugin;
    private FileConfiguration config;
    
    public ConfigManager(PrisonCoreY plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    
    public void reload() {
        this.config = plugin.getConfig();
    }
    
    public boolean debug() {
        return config.getBoolean("debug", false);
    }
    
    public String getDrugRegion() {
        return config.getString("drug-region", "drugs_farm");
    }
    
    public List<String> getDrugLore() {
        return config.getStringList("drug-lore");
    }
    
    public double getRankMultiplier(String rank) {
        return config.getDouble("rank-multipliers." + rank, 0.1);
    }
    
    public int getDrugLimit(String rank) {
        return config.getInt("drug-limits." + rank, 50);
    }
    
    public List<String> getEnchantBookLore() {
        return config.getStringList("enchant-book-lore");
    }
    
    public List<KeyFinderReward> getKeyFinderRewards() {
        List<KeyFinderReward> rewards = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection("keyfinder-rewards");
        
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection rewardSection = section.getConfigurationSection(key);
                if (rewardSection != null) {
                    double chance = rewardSection.getDouble("chance", 1.0);
                    String command = rewardSection.getString("command", "");
                    if (!command.isEmpty()) {
                        rewards.add(new KeyFinderReward(chance, command));
                    }
                }
            }
        } else {
            List<?> rewardList = config.getList("keyfinder-rewards");
            if (rewardList != null) {
                for (Object obj : rewardList) {
                    if (obj instanceof Map) {
                        Map<?, ?> rewardMap = (Map<?, ?>) obj;
                        Object chanceObj = rewardMap.get("chance");
                        Object commandObj = rewardMap.get("command");
                        
                        if (chanceObj != null && commandObj != null) {
                            double chance = Double.parseDouble(String.valueOf(chanceObj));
                            String command = String.valueOf(commandObj);
                            rewards.add(new KeyFinderReward(chance, command));
                        }
                    }
                }
            }
        }
        
        return rewards;
    }
    
    public Map<String, String> getCondenseConversions() {
        Map<String, String> conversions = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("condense-conversions");
        
        if (section != null) {
            for (String key : section.getKeys(false)) {
                conversions.put(key, section.getString(key));
            }
        }
        
        return conversions;
    }
    
    public String getMessage(String path) {
        String prefix = config.getString("messages.prefix", "");
        String message = config.getString("messages." + path, "&cMessage not found: " + path);
        return message.replace("%prefix%", prefix);
    }
    
    public static class KeyFinderReward {
        public final double chance;
        public final String command;
        
        public KeyFinderReward(double chance, String command) {
            this.chance = chance;
            this.command = command;
        }
    }
}