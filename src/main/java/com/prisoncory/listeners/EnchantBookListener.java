package com.prisoncory.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.prisoncory.PrisonCoreY;
import com.prisoncory.enchants.EnchantType;
import com.prisoncory.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public class EnchantBookListener implements Listener {
    
    private final PrisonCoreY plugin;
    
    public EnchantBookListener(PrisonCoreY plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBookUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || !plugin.getEnchantManager().isEnchantBook(item)) {
            return;
        }
        
        event.setCancelled(true);
        
        EnchantType enchantType = plugin.getEnchantManager().getEnchantType(item);
        int enchantLevel = plugin.getEnchantManager().getEnchantLevel(item);
        
        if (enchantType == null) {
            return;
        }
        
        ItemStack pickaxe = findDiamondPickaxe(player);
        
        if (pickaxe == null) {
            MessageUtil.send(player, plugin.getConfigManager().getMessage("no-pickaxe-found"));
            return;
        }
        
        if (!plugin.getEnchantManager().applyEnchantToPickaxe(pickaxe, enchantType, enchantLevel)) {
            MessageUtil.send(player, plugin.getConfigManager().getMessage("enchant-already-has"));
            return;
        }
        
        item.setAmount(item.getAmount() - 1);
        
        String message = plugin.getConfigManager().getMessage("enchant-applied")
                .replace("%enchant%", enchantType.getDisplayName())
                .replace("%level%", String.valueOf(enchantLevel));
        MessageUtil.send(player, message);
    }
    
    private ItemStack findDiamondPickaxe(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                XMaterial material = XMaterial.matchXMaterial(item.getType());
                if (material == XMaterial.DIAMOND_PICKAXE) {
                    return item;
                }
            }
        }
        return null;
    }
}