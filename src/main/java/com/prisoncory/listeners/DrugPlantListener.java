package com.prisoncory.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.prisoncory.PrisonCoreY;
import com.prisoncory.drugs.DrugType;
import com.prisoncory.utils.MessageUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class DrugPlantListener implements Listener {
    
    private final PrisonCoreY plugin;
    
    public DrugPlantListener(PrisonCoreY plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDrugPlant(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        ItemStack item = event.getItemInHand();
        
        if (!plugin.getDrugManager().isDrugItem(item)) {
            return;
        }
        
        if (!isInDrugRegion(block)) {
            event.setCancelled(true);
            MessageUtil.send(player, plugin.getConfigManager().getMessage("drug-region-only"));
            return;
        }
        
        if (!plugin.getPlantLimitManager().canPlant(player)) {
            event.setCancelled(true);
            int current = plugin.getPlantLimitManager().getCurrentPlants(player.getUniqueId());
            int max = plugin.getPlantLimitManager().getMaxPlants(player);
            String message = plugin.getConfigManager().getMessage("drug-limit-reached")
                    .replace("%current%", String.valueOf(current))
                    .replace("%max%", String.valueOf(max));
            MessageUtil.send(player, message);
            return;
        }
        
        plugin.getPlantLimitManager().addPlant(player.getUniqueId(), block.getLocation());
    }
    
    private boolean isInDrugRegion(Block block) {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            return true;
        }
        
        try {
            String requiredRegion = plugin.getConfigManager().getDrugRegion();
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(block.getLocation()));
            
            for (ProtectedRegion region : set) {
                if (region.getId().equalsIgnoreCase(requiredRegion)) {
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            plugin.getLogger().warning("Error checking WorldGuard region: " + e.getMessage());
            return true;
        }
    }
}