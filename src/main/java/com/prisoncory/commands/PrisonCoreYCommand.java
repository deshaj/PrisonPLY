package com.prisoncory.commands;

import com.prisoncory.PrisonCoreY;
import com.prisoncory.enchants.EnchantType;
import com.prisoncory.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PrisonCoreYCommand implements CommandExecutor, TabCompleter {
    
    private final PrisonCoreY plugin;
    
    public PrisonCoreYCommand(PrisonCoreY plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            MessageUtil.send(sender, "&8&m-------------------&r &6PrisonCoreY &8&m-------------------");
            MessageUtil.send(sender, "&e/pcy reload &7- Reload the configuration");
            MessageUtil.send(sender, "&e/pcy give <player> <enchant> <level> &7- Give enchant book");
            MessageUtil.send(sender, "&8&m------------------------------------------------");
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        if (subCommand.equals("reload")) {
            if (!sender.hasPermission("prisoncory.reload")) {
                MessageUtil.send(sender, plugin.getConfigManager().getMessage("no-permission"));
                return true;
            }
            
            plugin.reload();
            MessageUtil.send(sender, plugin.getConfigManager().getMessage("reload"));
            return true;
        }
        
        if (subCommand.equals("give")) {
            if (!sender.hasPermission("prisoncory.give")) {
                MessageUtil.send(sender, plugin.getConfigManager().getMessage("no-permission"));
                return true;
            }
            
            if (args.length < 4) {
                MessageUtil.send(sender, "&cUsage: /pcy give <player> <enchant> <level>");
                return true;
            }
            
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                MessageUtil.send(sender, plugin.getConfigManager().getMessage("invalid-player"));
                return true;
            }
            
            EnchantType enchantType = EnchantType.fromString(args[2]);
            if (enchantType == null) {
                MessageUtil.send(sender, plugin.getConfigManager().getMessage("invalid-enchant"));
                return true;
            }
            
            int level;
            try {
                level = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                MessageUtil.send(sender, plugin.getConfigManager().getMessage("invalid-level"));
                return true;
            }
            
            if (level < 1 || level > enchantType.getMaxLevel()) {
                MessageUtil.send(sender, plugin.getConfigManager().getMessage("invalid-level"));
                return true;
            }
            
            ItemStack book = plugin.getEnchantManager().createEnchantBook(enchantType, level);
            
            if (book == null) {
                MessageUtil.send(sender, "&cFailed to create enchant book!");
                return true;
            }
            
            target.getInventory().addItem(book);
            
            String senderMessage = plugin.getConfigManager().getMessage("gave-enchant-book")
                    .replace("%player%", target.getName())
                    .replace("%enchant%", enchantType.getDisplayName())
                    .replace("%level%", String.valueOf(level));
            MessageUtil.send(sender, senderMessage);
            
            String receiverMessage = plugin.getConfigManager().getMessage("received-enchant-book")
                    .replace("%enchant%", enchantType.getDisplayName())
                    .replace("%level%", String.valueOf(level));
            MessageUtil.send(target, receiverMessage);
            
            return true;
        }
        
        MessageUtil.send(sender, "&cUnknown subcommand. Use /pcy for help.");
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.add("reload");
            completions.add("give");
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            for (EnchantType type : EnchantType.values()) {
                completions.add(type.name());
            }
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (args.length == 4 && args[0].equalsIgnoreCase("give")) {
            EnchantType enchantType = EnchantType.fromString(args[2]);
            if (enchantType != null) {
                for (int i = 1; i <= enchantType.getMaxLevel(); i++) {
                    completions.add(String.valueOf(i));
                }
            }
            return completions;
        }
        
        return completions;
    }
}