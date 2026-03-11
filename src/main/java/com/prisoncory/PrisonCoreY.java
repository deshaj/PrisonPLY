package com.prisoncory;

import com.prisoncory.commands.PrisonCoreYCommand;
import com.prisoncory.listeners.DrugBreakListener;
import com.prisoncory.listeners.DrugGrowthListener;
import com.prisoncory.listeners.DrugPlantListener;
import com.prisoncory.listeners.EnchantBookListener;
import com.prisoncory.listeners.PickaxeMineListener;
import com.prisoncory.managers.ConfigManager;
import com.prisoncory.managers.DrugManager;
import com.prisoncory.managers.EnchantManager;
import com.prisoncory.managers.GrowthManager;
import com.prisoncory.managers.PlantLimitManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PrisonCoreY extends JavaPlugin {
    
    private ConfigManager configManager;
    private DrugManager drugManager;
    private PlantLimitManager plantLimitManager;
    private GrowthManager growthManager;
    private EnchantManager enchantManager;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        this.configManager = new ConfigManager(this);
        this.drugManager = new DrugManager(this);
        this.plantLimitManager = new PlantLimitManager(this);
        this.growthManager = new GrowthManager(this);
        this.enchantManager = new EnchantManager(this);
        
        registerListeners();
        registerCommands();
        
        if (!checkDependencies()) {
            getLogger().warning("WorldGuard or LuckPerms not found. Some features may not work!");
        }
        
        getLogger().info("PrisonCoreY has been enabled!");
    }
    
    @Override
    public void onDisable() {
        if (plantLimitManager != null) {
            plantLimitManager.saveAllData();
        }
        getLogger().info("PrisonCoreY has been disabled!");
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new DrugPlantListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DrugBreakListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DrugGrowthListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EnchantBookListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PickaxeMineListener(this), this);
    }
    
    private void registerCommands() {
        getCommand("prisoncory").setExecutor(new PrisonCoreYCommand(this));
    }
    
    private boolean checkDependencies() {
        boolean worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard") != null;
        boolean luckPerms = Bukkit.getPluginManager().getPlugin("LuckPerms") != null;
        return worldGuard && luckPerms;
    }
    
    public void reload() {
        reloadConfig();
        configManager.reload();
        plantLimitManager.saveAllData();
    }
}