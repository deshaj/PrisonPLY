package com.prisoncory.drugs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrugData {
    private UUID playerId;
    private Set<String> plantLocations = new HashSet<>();
    
    public void addPlant(Location location) {
        plantLocations.add(locationToString(location));
    }
    
    public void removePlant(Location location) {
        plantLocations.remove(locationToString(location));
    }
    
    public boolean hasPlant(Location location) {
        return plantLocations.contains(locationToString(location));
    }
    
    public int getPlantCount() {
        return plantLocations.size();
    }
    
    private String locationToString(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }
}