/**
 * User: YBogomolov
 * Date: 15.07.11
 * Time: 16:06
 */
package com.github.doodlez.bukkit.globalquest.utilities;

import com.github.doodlez.bukkit.globalquest.GlobalQuestPlugin;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Class, which encapsulates helper methods for working with locations, chunks and spheres.
 */
public class LocationHelper {
    /**
     * Determines coordinates of the sphere for given world and block.
     * @param theWorld Current world.
     * @param theBlock Block for which needed to calculate sphere center coordinates.
     * @return Location of the center of the sphere.
     */
    public static Location GetSphereCenterLocation(World theWorld, Block theBlock) {
        Chunk chunk = theBlock.getChunk();

        int baseChunkX = theBlock.getX() & 0x000F;
        int baseChunkZ = theBlock.getZ() & 0x000F;

        // Magic number, taken from Yuki's code:
        final int GRID_SIZE = 9;
        int centerChunkX = (chunk.getX() - (int)Math.floor(Math.IEEEremainder(chunk.getX(), GRID_SIZE)) << 4) + 8;
        int centerChunkZ = (chunk.getZ() - (int)Math.floor(Math.IEEEremainder(chunk.getZ(), GRID_SIZE)) << 4) + 8;

        if (GlobalQuestPlugin.isDebugEnabled)
            System.out.print("CenterChunk: " + centerChunkX + ", " + centerChunkZ);

        int centerX = theBlock.getX() + (centerChunkX - baseChunkX);
        int centerZ = theBlock.getZ() + (centerChunkZ - baseChunkZ);

        return new Location(theWorld, centerX, theBlock.getY(), centerZ);
    }
}
