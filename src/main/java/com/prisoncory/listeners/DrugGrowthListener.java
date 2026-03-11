package com.prisoncory.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.prisoncory.PrisonCoreY;
import com.prisoncory.drugs.DrugData;
import com.prisoncory.drugs.DrugType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

import java.util.UUID;

public class DrugGrowthListener implements Listener {
    
    private final PrisonCoreY plugin;
    
    public DrugGrowthListener(PrisonCoreY plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDrugGrow(BlockGrowEvent event) {
        Block block = event.getBlock();
        XMaterial material = XMaterial.matchXMaterial(block.getType());
        
        DrugType drugType = DrugType.fromMaterial(material);
        if (drugType == null) {
            return;
        }
        
        Location loc = block.getLocation();
        UUID plantOwner = findPlantOwner(loc);
        
        if (plantOwner == null) {
            return;
        }
        
        Player player = Bukkit.getPlayer(plantOwner);
        if (player == null) {
            return;
        }
        
        if (!plugin.getGrowthManager().shouldGrow(player)) {
            event.setCancelled(true);
        }
    }
    
    private UUID findPlantOwner(Location location) {
        for (UUID playerId : plugin.getPlantLimitManager().playerData.keySet()) {
            DrugData data = plugin.getPlantLimitManager().getPlayerData(playerId);
            if (data.hasPlant(location)) {
                return playerId;
            }
        }
        return null;
    }
}