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
    public int airbaseRadius;

    /**
     * List of special blocks, one of them controls lightning strikes on the other.
     */
    public ArrayList<LightningPairedBlocks> lightningPairedBlocksList = new ArrayList<LightningPairedBlocks>();

    /**
     * Frequency of lightning strikes in Minecraft time. 1 second IRL == 20 ticks in game.
     */
    public int lightningFrequency;

    /**
     * Source block material for lightning-paired blocks.
     * @see LightningPairedBlocks
     */
    public int lightningSourceId;

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
                <= Math.pow(theAirbase.airbaseRadius, 2.0);
    }
}