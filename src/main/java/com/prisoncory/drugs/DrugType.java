package com.prisoncory.drugs;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;

@Getter
public enum DrugType {
    BLOODROOT("Bloodroot", XMaterial.WHEAT, XMaterial.WHEAT_SEEDS),
    WHITE_POWDER("White Powder", XMaterial.SUGAR_CANE, XMaterial.SUGAR_CANE),
    GREEN_VINES("Green Vines", XMaterial.CACTUS, XMaterial.CACTUS);
    
    private final String displayName;
    private final XMaterial cropMaterial;
    private final XMaterial seedMaterial;
    
    DrugType(String displayName, XMaterial cropMaterial, XMaterial seedMaterial) {
        this.displayName = displayName;
        this.cropMaterial = cropMaterial;
        this.seedMaterial = seedMaterial;
    }
    
    public static DrugType fromMaterial(XMaterial material) {
        for (DrugType type : values()) {
            if (type.seedMaterial == material || type.cropMaterial == material) {
                return type;
            }
        }
        return null;
    }
}