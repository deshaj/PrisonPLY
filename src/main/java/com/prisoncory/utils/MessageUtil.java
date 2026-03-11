package com.prisoncory.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageUtil {
    
    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public static void send(CommandSender sender, String message) {
        sender.sendMessage(color(message));
    }
}