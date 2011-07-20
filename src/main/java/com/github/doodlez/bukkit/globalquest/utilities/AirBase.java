package com.github.doodlez.bukkit.globalquest.utilities;

import com.github.doodlez.bukkit.globalquest.GlobalQuestPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
    public boolean domeEnabled = true;

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

    /**
     * Toggles defencive glass dome on and off.
     * @param world World where executes this command.
     * @param airBase AirBase to toggle dome over.
     * @param emergencyCase States if dome should function as usual or in emergency mode.
     */
    public static void toggleGlass(World world, AirBase airBase, boolean emergencyCase) {
        Block airBaseCenterBlock = world.getBlockAt(airBase.airbaseCenterCoordinates);

        Material domeMaterial = emergencyCase ? Material.OBSIDIAN : Material.GLASS;

        // Center coordinates — just shorter references.
        int x0 = airBaseCenterBlock.getX();
        int y0 = airBaseCenterBlock.getY();
        int z0 = airBaseCenterBlock.getZ();

        if (GlobalQuestPlugin.isDebugEnabled)
            System.out.print("Building sphere: at (" + x0 + "," + y0 + "," + z0 + "), R = " + airBase.domeRadius);

        for (int x = -airBase.domeRadius; x <= airBase.domeRadius; ++x) {
            for (int y = -airBase.domeRadius; y <= airBase.domeRadius; ++y) {
                for (int z = -airBase.domeRadius; z <= airBase.domeRadius; ++z) {
                    int X = x + x0;
                    int Y = y + y0;
                    int Z = z + z0;
                    if (AirBase.blockBelongsToDome(x, y, z, airBase.domeRadius, airBase.domeThickness)) {
                        if (airBase.domeEnabled) {
                            if (world.getBlockAt(X, Y, Z).getType() == Material.AIR)
                                world.getBlockAt(X, Y, Z).setType(domeMaterial);

                            if (world.getBlockAt(X, Y, Z).getType() == Material.GLASS && emergencyCase)
                                world.getBlockAt(X, Y, Z).setType(domeMaterial);
                        }
                        else {
                            for (int dx = -1; dx <= 1; ++dx)
                                for (int dy = -1; dy <= 1; ++dy)
                                    for (int dz = -1; dz <= 1; ++dz)
                                        if ((world.getBlockAt(X + dx, Y + dy, Z + dz).getType() == Material.GLASS)
                                            || (world.getBlockAt(X + dx, Y + dy, Z + dz).getType() == Material.OBSIDIAN))
                                            world.getBlockAt(X + dx, Y + dy, Z + dz).setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }
}