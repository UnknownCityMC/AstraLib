package de.unknowncity.astralib.paper.api.region;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SphericalRegion implements Region{
    private final Location center;
    private final int radius;

    public SphericalRegion(Location center, int radius) {
        this.center = center;
        this.radius = radius;
    }

    public boolean isInRegion(Player player) {
        if (!player.getWorld().equals(center.getWorld())) {
            return false;
        }
        return !(player.getLocation().distanceSquared(center) > radius * radius);
    }
}
