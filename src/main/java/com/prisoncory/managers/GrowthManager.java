package com.prisoncory.managers;

import com.prisoncory.PrisonCoreY;
import org.bukkit.entity.Player;

public class GrowthManager {
    
    private final PrisonCoreY plugin;
    
    public GrowthManager(PrisonCoreY plugin) {
        this.plugin = plugin;
    }
    
    public double getGrowthMultiplier(Player player) {
        String rank = plugin.getPlantLimitManager().getPlayerRank(player);
        return plugin.getConfigManager().getRankMultiplier(rank);
    }
    
    public boolean shouldGrow(Player player) {
        double multiplier = getGrowthMultiplier(player);
        
        if (multiplier >= 1.0) {
            return true;
        }
        
        return Math.random() < multiplier;
    }
}