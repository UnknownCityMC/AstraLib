package de.unknowncity.astralib.paper.api.region;

import de.unknowncity.astralib.common.util.AstraMath;
import de.unknowncity.astralib.paper.api.exception.region.RegionCreateException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CubicalRegion implements Region {
    private final Location cornerOne, cornerTwo;

    public CubicalRegion(Location cornerOne, Location cornerTwo) {
        if (!cornerOne.getWorld().equals(cornerTwo.getWorld())) {
            throw new RegionCreateException("Both corners of cubical region must be in same world");
        }
        this.cornerOne = cornerOne;
        this.cornerTwo = cornerTwo;
    }

    @Override
    public boolean isInRegion(Player player) {
        if (!player.getWorld().equals(cornerOne.getWorld())) {
            return false;
        }
        var loc = player.getLocation();

        return AstraMath.isBetween(loc.x(), cornerOne.x(), cornerTwo.x()) &&
                AstraMath.isBetween(loc.y(), cornerOne.y(), cornerTwo.y()) &&
                AstraMath.isBetween(loc.z(), cornerOne.z(), cornerTwo.z());
    }
}
