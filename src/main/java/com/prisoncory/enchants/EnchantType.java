package com.prisoncory.enchants;

import lombok.Getter;

@Getter
public enum EnchantType {
    CUBED("Cubed", 3),
    SMELTING("Smelting", 1),
    KEY_FINDER("Key Finder", 5),
    CONDENSE("Condense", 1);
    
    private final String displayName;
    private final int maxLevel;
    
    EnchantType(String displayName, int maxLevel) {
        this.displayName = displayName;
        this.maxLevel = maxLevel;
    }
    
    public static EnchantType fromString(String name) {
        for (EnchantType type : values()) {
            if (type.name().equalsIgnoreCase(name) || type.displayName.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
    
    public String getDisplayName(int level) {
        if (this == CUBED) {
            String[] romans = {"", "I", "II", "III"};
            return displayName + " " + (level <= romans.length - 1 ? romans[level] : level);
        } else if (this == KEY_FINDER) {
            return displayName + " " + level;
        } else {
            return displayName;
        }
    }
}