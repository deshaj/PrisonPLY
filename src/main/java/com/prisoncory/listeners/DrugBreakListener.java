package com.prisoncory.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.prisoncory.PrisonCoreY;
import com.prisoncory.drugs.DrugType;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.UUID;

public class DrugBreakListener implements Listener {
    
    private final PrisonCoreY plugin;
    
    public DrugBreakListener(PrisonCoreY plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDrugBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        XMaterial material = XMaterial.matchXMaterial(block.getType());
        
        DrugType drugType = DrugType.fromMaterial(material);
        if (drugType == null) {
            return;
        }
        
        for (UUID playerId : plugin.getPlantLimitManager().playerData.keySet()) {
            plugin.getPlantLimitManager().removePlant(playerId, block.getLocation());
        }
    }
}