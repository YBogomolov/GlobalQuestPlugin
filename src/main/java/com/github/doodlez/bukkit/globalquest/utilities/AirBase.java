package com.github.doodlez.bukkit.globalquest.utilities;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;

/**
 * Class that encapsulates all information about Isaak Breen's airbase.
 */
public class AirBase {
    /**
     * Center coordinates of the airbase.
     */
    public static Location airbaseCenterCoordinates;

    /**
     * Radius of the airbase sphere.
     */
    public static int airbaseRadius;

    /**
     * List of special blocks, one of them controls lightning strikes on the other.
     */
    public static ArrayList<LightningPairedBlocks> lightningPairedBlocksList = new ArrayList<LightningPairedBlocks>();

    /**
     * Frequency of lightning strikes in Minecraft time. 1 second IRL == 20 ticks in game.
     */
    public static int lightningFrequency;

    /**
     * Source block material for lightning-paired blocks.
     * @see LightningPairedBlocks
     */
    public static int lightningSourceId;

    /**
     * Determines if the block given belongs to the airbase.
     * @param block The block to check.
     * @return True, if block belongs to airbase territory, and false otherwise.
     */
    public static boolean blockBelongsToAirbase(Block block) {
        return (Math.pow(block.getX() - airbaseCenterCoordinates.getX(), 2.0) +
                Math.pow(block.getZ() - airbaseCenterCoordinates.getZ(), 2.0)) <= Math.pow(airbaseRadius, 2.0);
    }
}