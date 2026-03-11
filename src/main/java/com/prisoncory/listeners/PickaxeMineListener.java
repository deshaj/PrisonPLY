package com.prisoncory.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.prisoncory.PrisonCoreY;
import com.prisoncory.enchants.EnchantType;
import com.prisoncory.managers.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickaxeMineListener implements Listener {
    
    private final PrisonCoreY plugin;
    private final Map<XMaterial, XMaterial> smeltingMap;
    
    public PickaxeMineListener(PrisonCoreY plugin) {
        this.plugin = plugin;
        this.smeltingMap = new HashMap<>();
        initSmeltingMap();
    }
    
    private void initSmeltingMap() {
        smeltingMap.put(XMaterial.IRON_ORE, XMaterial.IRON_INGOT);
        smeltingMap.put(XMaterial.DEEPSLATE_IRON_ORE, XMaterial.IRON_INGOT);
        smeltingMap.put(XMaterial.GOLD_ORE, XMaterial.GOLD_INGOT);
        smeltingMap.put(XMaterial.DEEPSLATE_GOLD_ORE, XMaterial.GOLD_INGOT);
        smeltingMap.put(XMaterial.COPPER_ORE, XMaterial.COPPER_INGOT);
        smeltingMap.put(XMaterial.DEEPSLATE_COPPER_ORE, XMaterial.COPPER_INGOT);
        smeltingMap.put(XMaterial.ANCIENT_DEBRIS, XMaterial.NETHERITE_SCRAP);
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack tool = player.getInventory().getItemInMainHand();
        
        XMaterial toolMaterial = XMaterial.matchXMaterial(tool.getType());
        if (toolMaterial != XMaterial.DIAMOND_PICKAXE) {
            return;
        }
        
        handleCubedEnchant(event, player, block, tool);
        handleSmeltingEnchant(event, block, tool);
        handleKeyFinderEnchant(player, tool);
        handleCondenseEnchant(event, block, tool);
    }
    
    private void handleCubedEnchant(BlockBreakEvent event, Player player, Block center, ItemStack tool) {
        if (!plugin.getEnchantManager().hasEnchant(tool, EnchantType.CUBED)) {
            return;
        }
        
        int level = plugin.getEnchantManager().getPickaxeEnchantLevel(tool, EnchantType.CUBED);
        int radius = level + 1;
        
        List<Block> blocksToBreak = new ArrayList<>();
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }
                    
                    Block block = center.getRelative(x, y, z);
                    
                    if (block.getType() != Material.AIR && block.getType() != Material.BEDROCK) {
                        blocksToBreak.add(block);
                    }
                }
            }
        }
        
        for (Block block : blocksToBreak) {
            block.breakNaturally(tool);
        }
    }
    
    private void handleSmeltingEnchant(BlockBreakEvent event, Block block, ItemStack tool) {
        if (!plugin.getEnchantManager().hasEnchant(tool, EnchantType.SMELTING)) {
            return;
        }
        
        XMaterial blockMaterial = XMaterial.matchXMaterial(block.getType());
        XMaterial smeltedMaterial = smeltingMap.get(blockMaterial);
        
        if (smeltedMaterial == null) {
            return;
        }
        
        event.setDropItems(false);
        
        Collection<ItemStack> drops = block.getDrops(tool);
        Location loc = block.getLocation().add(0.5, 0.5, 0.5);
        
        for (ItemStack drop : drops) {
            ItemStack smelted = smeltedMaterial.parseItem();
            if (smelted != null) {
                smelted.setAmount(drop.getAmount());
                block.getWorld().dropItemNaturally(loc, smelted);
            }
        }
    }
    
    private void handleKeyFinderEnchant(Player player, ItemStack tool) {
        if (!plugin.getEnchantManager().hasEnchant(tool, EnchantType.KEY_FINDER)) {
            return;
        }
        
        int level = plugin.getEnchantManager().getPickaxeEnchantLevel(tool, EnchantType.KEY_FINDER);
        double levelMultiplier = 1.0 + (level * 0.1);
        
        List<ConfigManager.KeyFinderReward> rewards = plugin.getConfigManager().getKeyFinderRewards();
        
        for (ConfigManager.KeyFinderReward reward : rewards) {
            double adjustedChance = reward.chance * levelMultiplier;
            
            if (Math.random() * 100 < adjustedChance) {
                String command = reward.command.replace("%player%", player.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                XSound.matchXSound("ENTITY_PLAYER_LEVELUP").ifPresent(sound -> sound.play(player));
                break;
            }
        }
    }
    
    private void handleCondenseEnchant(BlockBreakEvent event, Block block, ItemStack tool) {
        if (!plugin.getEnchantManager().hasEnchant(tool, EnchantType.CONDENSE)) {
            return;
        }
        
        XMaterial blockMaterial = XMaterial.matchXMaterial(block.getType());
        Map<String, String> conversions = plugin.getConfigManager().getCondenseConversions();
        
        event.setDropItems(false);
        
        Collection<ItemStack> drops = block.getDrops(tool);
        Location loc = block.getLocation().add(0.5, 0.5, 0.5);
        
        for (ItemStack drop : drops) {
            XMaterial dropMaterial = XMaterial.matchXMaterial(drop.getType());
            String convertTo = conversions.get(dropMaterial.name());
            
            if (convertTo != null && drop.getAmount() >= 9) {
                XMaterial blockForm = XMaterial.matchXMaterial(convertTo).orElse(null);
                
                if (blockForm != null) {
                    int blockCount = drop.getAmount() / 9;
                    int remainder = drop.getAmount() % 9;
                    
                    if (blockCount > 0) {
                        ItemStack blockItem = blockForm.parseItem();
                        if (blockItem != null) {
                            blockItem.setAmount(blockCount);
                            block.getWorld().dropItemNaturally(loc, blockItem);
                        }
                    }
                    
                    if (remainder > 0) {
                        ItemStack remainderItem = drop.clone();
                        remainderItem.setAmount(remainder);
                        block.getWorld().dropItemNaturally(loc, remainderItem);
                    }
                } else {
                    block.getWorld().dropItemNaturally(loc, drop);
                }
            } else {
                block.getWorld().dropItemNaturally(loc, drop);
            }
        }
    }
}