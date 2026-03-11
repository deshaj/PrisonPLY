package com.prisoncory.utils;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {
    
    private final ItemStack item;
    private final ItemMeta meta;
    
    public ItemBuilder(XMaterial material) {
        this.item = material.parseItem();
        this.meta = item != null ? item.getItemMeta() : null;
    }
    
    public ItemBuilder name(String name) {
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        }
        return this;
    }
    
    public ItemBuilder lore(String... lines) {
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            for (String line : lines) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(lore);
        }
        return this;
    }
    
    public ItemBuilder lore(List<String> lines) {
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            for (String line : lines) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(lore);
        }
        return this;
    }
    
    public ItemBuilder amount(int amount) {
        if (item != null) {
            item.setAmount(amount);
        }
        return this;
    }
    
    public ItemStack build() {
        if (item != null && meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }
}