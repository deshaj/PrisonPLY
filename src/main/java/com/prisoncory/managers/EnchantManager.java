package com.prisoncory.managers;

import com.cryptomorin.xseries.XMaterial;
import com.prisoncory.PrisonCoreY;
import com.prisoncory.enchants.EnchantType;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class EnchantManager {
    
    private final PrisonCoreY plugin;
    private final NamespacedKey enchantKey;
    private final NamespacedKey levelKey;
    private final NamespacedKey bookKey;
    
    public EnchantManager(PrisonCoreY plugin) {
        this.plugin = plugin;
        this.enchantKey = new NamespacedKey(plugin, "custom_enchant");
        this.levelKey = new NamespacedKey(plugin, "enchant_level");
        this.bookKey = new NamespacedKey(plugin, "enchant_book");
    }
    
    public ItemStack createEnchantBook(EnchantType type, int level) {
        ItemStack book = XMaterial.BOOK.parseItem();
        
        if (book == null) {
            return null;
        }
        
        ItemMeta meta = book.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6" + type.getDisplayName(level) + " Enchant"));
            
            List<String> lore = new ArrayList<>();
            for (String line : plugin.getConfigManager().getEnchantBookLore()) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            
            lore.add("");
            lore.add(ChatColor.translateAlternateColorCodes('&', "&eEnchant: &f" + type.getDisplayName(level)));
            
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(bookKey, PersistentDataType.STRING, "true");
            meta.getPersistentDataContainer().set(enchantKey, PersistentDataType.STRING, type.name());
            meta.getPersistentDataContainer().set(levelKey, PersistentDataType.INTEGER, level);
            
            book.setItemMeta(meta);
        }
        
        return book;
    }
    
    public boolean isEnchantBook(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        return item.getItemMeta().getPersistentDataContainer().has(bookKey, PersistentDataType.STRING);
    }
    
    public EnchantType getEnchantType(ItemStack item) {
        if (!isEnchantBook(item)) {
            return null;
        }
        
        String typeName = item.getItemMeta().getPersistentDataContainer().get(enchantKey, PersistentDataType.STRING);
        
        if (typeName == null) {
            return null;
        }
        
        try {
            return EnchantType.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    public int getEnchantLevel(ItemStack item) {
        if (!isEnchantBook(item)) {
            return 0;
        }
        
        Integer level = item.getItemMeta().getPersistentDataContainer().get(levelKey, PersistentDataType.INTEGER);
        return level != null ? level : 0;
    }
    
    public boolean applyEnchantToPickaxe(ItemStack pickaxe, EnchantType type, int level) {
        if (pickaxe == null || !pickaxe.hasItemMeta()) {
            return false;
        }
        
        XMaterial pickaxeMaterial = XMaterial.matchXMaterial(pickaxe.getType());
        if (pickaxeMaterial != XMaterial.DIAMOND_PICKAXE) {
            return false;
        }
        
        ItemMeta meta = pickaxe.getItemMeta();
        
        Integer existingLevel = meta.getPersistentDataContainer().get(new NamespacedKey(plugin, "enchant_" + type.name()), PersistentDataType.INTEGER);
        if (existingLevel != null && existingLevel >= level) {
            return false;
        }
        
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "enchant_" + type.name()), PersistentDataType.INTEGER, level);
        
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        
        String enchantLine = ChatColor.translateAlternateColorCodes('&', "&d" + type.getDisplayName(level));
        
        if (existingLevel != null) {
            String oldEnchantLine = ChatColor.translateAlternateColorCodes('&', "&d" + type.getDisplayName(existingLevel));
            lore.remove(oldEnchantLine);
        }
        
        lore.add(enchantLine);
        meta.setLore(lore);
        
        pickaxe.setItemMeta(meta);
        return true;
    }
    
    public boolean hasEnchant(ItemStack item, EnchantType type) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "enchant_" + type.name()), PersistentDataType.INTEGER);
    }
    
    public int getPickaxeEnchantLevel(ItemStack item, EnchantType type) {
        if (!hasEnchant(item, type)) {
            return 0;
        }
        
        Integer level = item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "enchant_" + type.name()), PersistentDataType.INTEGER);
        return level != null ? level : 0;
    }
}