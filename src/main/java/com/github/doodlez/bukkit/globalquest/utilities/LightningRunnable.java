/**
 * User: YBogomolov
 * Date: 16.07.11
 * Time: 18:33
 */
package com.github.doodlez.bukkit.globalquest.utilities;

import com.github.doodlez.bukkit.globalquest.GlobalQuestPlugin;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Implements custom Runnable with access to specific world.
 * Allows to run scheduled tasks with different frequency for different worlds.
 */
public class LightningRunnable implements Runnable {
    /**
     * The world this task must be ran in.
     */
    private World world;

    /**
     * Constructor. Creates this object and sets private fields.
     * @param world The world this task must be executed in.
     */
    public LightningRunnable(World world) {
        this.world = world;
    }

    /**
     * Method which is ran by scheduler.
     */
    public void run() {
        if (!GlobalQuestPlugin.airBases.containsKey(world)) {
            return;
        }

        boolean previousHasStorm = world.hasStorm();
        boolean allBlocksDestroyed = true;
        AirBase airBase = GlobalQuestPlugin.airBases.get(world);

        for (LightningPairedBlocks blocks: airBase.lightningPairedBlocksList) {
            Block block = world.getBlockAt(blocks.getSourceBlock());
            if (block.getTypeId() == airBase.lightningSourceId) {
                allBlocksDestroyed = false;

                world.setStorm(true);

                if (GlobalQuestPlugin.isDebugEnabled) {
                    System.out.print("Lightning strike at " + blocks.getTargetBlock().getX() + ", " +
                                                              blocks.getTargetBlock().getY() + ", " +
                                                              blocks.getTargetBlock().getZ());
                }
                world.strikeLightning(blocks.getTargetBlock());
            }
        }

        if (allBlocksDestroyed)
            world.setStorm(previousHasStorm);
    }
}
