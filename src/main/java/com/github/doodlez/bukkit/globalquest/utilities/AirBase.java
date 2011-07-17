package com.github.doodlez.bukkit.globalquest.utilities;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;

/**
 * Class that encapsulates all information about Isaak Breen's airbase.
 */
public class AirBase {
    /**
     * World name, to which this airbase belongs.
     */
    public String worldName;
    
    /**
     * Center coordinates of the airbase.
     */
    public Location airbaseCenterCoordinates;

    /**
     * Radius of the airbase sphere.
     */
    public int domeRadius;

    /**
     * List of special blocks, one of them controls lightning strikes on the other.
     */
    public ArrayList<LightningPairedBlocks> lightningPairedBlocksList = new ArrayList<LightningPairedBlocks>();

    /**
     * Frequency of lightning strikes in Minecraft time. 1 second IRL == 20 ticks in game.
     */
    public int lightningFrequency;

    /**
     * Is glass dome enabled?
     */
    public boolean glassEnabled = true;

    /**
     * Source block material for lightning-paired blocks.
     * @see LightningPairedBlocks
     */
    public int lightningSourceId;

    /**
     * Thickness (approximate) of dome in blocks.
     */
    public double domeThickness;

    public AirBase(String worldName) {
        this.worldName = worldName;
        lightningPairedBlocksList = new ArrayList<LightningPairedBlocks>();
    }

    /**
     * Determines if the block given belongs to the airbase.
     * @param block The block to check.
     * @param theAirbase THe airbase to check block belonging upon.
     * @return True, if block belongs to airbase territory, and false otherwise.
     */
    public static boolean blockBelongsToAirbase(Block block, AirBase theAirbase) {
        return (Math.pow(block.getX() - theAirbase.airbaseCenterCoordinates.getX(), 2.0) +
                Math.pow(block.getZ() - theAirbase.airbaseCenterCoordinates.getZ(), 2.0))
                <= Math.pow(theAirbase.domeRadius, 2.0);
    }

    /**
     * Tests if block with specified coordinates belongs to the dome.
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     * @param radius Dome radius to test upon.
     * @param thickness Dome thickness.
     * @return True, if block belongs to the dome, and false otherwise.
     */
    public static boolean blockBelongsToDome(int x, int y, int z, int radius, double thickness) {
        double coefficient = Math.abs(Math.sqrt((x * x) + (y * y) + (z * z)) - (double)radius);
        return (coefficient <= thickness) && (coefficient >= 0.0D);
    }
}