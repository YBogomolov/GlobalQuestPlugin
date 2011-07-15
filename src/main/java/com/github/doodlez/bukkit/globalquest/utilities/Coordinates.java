/**
 * User: YBogomolov
 * Date: 15.07.11
 * Time: 1:34
 */
package com.github.doodlez.bukkit.globalquest.utilities;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

/**
 * Class, which represents the coordinates of some block, chunk, etc.
 */
public class Coordinates {
    /**
     * X coordinate.
     */
    public int X;

    /**
     * Y coordinate. Used rarely.
     */
    public int Y;

    /**
     * Z coordinate.
     */
    public int Z;

    /**
     * Default constructor.
     */
    public Coordinates() {
        this.X = this.Y = this.Z = 0;
    }

    /**
     * Parameterized constructor.
     * @param X X coordinate.
     * @param Y Y coordinate.
     * @param Z Z coordinate.
     */
    public Coordinates(int X, int Y, int Z) {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    /**
     * Calculates coordinates of the chunk.
     * @param theBlock Block, for which needed to calculate chunk center coordinates.
     * @return Coordinates of the chunk's center.
     */
    public static Coordinates GetChunkCenterCoordinates(Block theBlock) {
        Chunk chunk = theBlock.getChunk();
        Coordinates baseWorldCoord = new Coordinates(theBlock.getX(), theBlock.getY(), theBlock.getZ());

        int baseChunkX = baseWorldCoord.X & 0x000F;
        int baseChunkZ = baseWorldCoord.Z & 0x000F;

        final int GRID_SIZE = 9;
        int centerChunkX = (chunk.getX() - (int)Math.floor(Math.IEEEremainder(chunk.getX(), GRID_SIZE)) << 4) + 8;
        int centerChunkZ = (chunk.getZ() - (int)Math.floor(Math.IEEEremainder(chunk.getZ(), GRID_SIZE)) << 4) + 8;

        System.out.print("CenterChunk: " + centerChunkX + ", " + centerChunkZ);

        int centerX = baseWorldCoord.X + (centerChunkX - baseChunkX);
        int centerZ = baseWorldCoord.Z + (centerChunkZ - baseChunkZ);

        return new Coordinates(centerX, 0, centerZ);
    }
}
