package com.github.doodlez.bukkit.globalquest.utilities;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * User: YBogomolov
 * Date: 15.07.11
 * Time: 16:06
 */
public class LocationHelper {
    public static Location GetChunkCenterLocation(World theWorld, Block theBlock) {
        Chunk chunk = theBlock.getChunk();

        int baseChunkX = theBlock.getX() & 0x000F;
        int baseChunkZ = theBlock.getZ() & 0x000F;

        final int GRID_SIZE = 9;
        int centerChunkX = (chunk.getX() - (int)Math.floor(Math.IEEEremainder(chunk.getX(), GRID_SIZE)) << 4) + 8;
        int centerChunkZ = (chunk.getZ() - (int)Math.floor(Math.IEEEremainder(chunk.getZ(), GRID_SIZE)) << 4) + 8;

        System.out.print("CenterChunk: " + centerChunkX + ", " + centerChunkZ);

        int centerX = theBlock.getX() + (centerChunkX - baseChunkX);
        int centerZ = theBlock.getZ() + (centerChunkZ - baseChunkZ);

        return new Location(theWorld, centerX, theBlock.getY(), centerZ);
    }
}
