package com.prisoncory.managers;

import com.cryptomorin.xseries.XMaterial;
import com.prisoncory.PrisonCoreY;
import com.prisoncory.drugs.DrugType;
import com.prisoncory.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.List;

public class DrugManager {
    
    private final PrisonCoreY plugin;
    private final NamespacedKey drugKey;
    
    public DrugManager(PrisonCoreY plugin) {
        this.plugin = plugin;
        this.drugKey = new NamespacedKey(plugin, "drug_type");
    }
    
    public ItemStack createDrugItem(DrugType type, int amount) {
        XMaterial material = type.getSeedMaterial();
        ItemStack item = material.parseItem();
        
        if (item == null) {
            return null;
        }
        
        item.setAmount(amount);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6" + type.getDisplayName()));
            
            List<String> lore = new ArrayList<>();
            for (String line : plugin.getConfigManager().getDrugLore()) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(lore);
            
            meta.getPersistentDataContainer().set(drugKey, PersistentDataType.STRING, type.name());
            item.setItemMeta(meta);
        }
        
        return item;
    }
    
    public boolean isDrugItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        return item.getItemMeta().getPersistentDataContainer().has(drugKey, PersistentDataType.STRING);
    }
    
    public DrugType getDrugType(ItemStack item) {
        if (!isDrugItem(item)) {
            return null;
        }
        
        String typeName = item.getItemMeta().getPersistentDataContainer().get(drugKey, PersistentDataType.STRING);
        
        if (typeName == null) {
            return null;
        }
        
        try {
            return DrugType.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    public boolean isDrugBlock(XMaterial material) {
        return DrugType.fromMaterial(material) != null;
    }
}