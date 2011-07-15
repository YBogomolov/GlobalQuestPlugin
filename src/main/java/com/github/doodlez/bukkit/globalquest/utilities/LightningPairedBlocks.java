/**
 * User: YBogomolov
 * Date: 15.07.11
 * Time: 13:46
 */

package com.github.doodlez.bukkit.globalquest.utilities;

import org.bukkit.Location;

/**
 * Represents lightning paired blocks — is source is not broken, target is hit by lightning.
 */
public class LightningPairedBlocks {
    /**
     * Source block — while it's not broken, the target is hit by the lightning.
     */
    private Location sourceBlock;

    /**
     * Target block, which is hit by the lightning, while source block not broken.
     */
    private Location targetBlock;

    /**
     * Getter for source block.
     * @return Source block location.
     */
    public Location getSourceBlock() {
        return sourceBlock;
    }

    /**
     * Setter for source block.
     * @param sourceBlock New source block location.
     */
    public void setSourceBlock(Location sourceBlock) {
        this.sourceBlock = sourceBlock;
    }

    /**
     * Getter for target block.
     * @return Target block location.
     */
    public Location getTargetBlock() {
        return targetBlock;
    }

    /**
     * Setter for target block.
     * @param targetBlock New target block location.
     */
    public void setTargetBlock(Location targetBlock) {
        this.targetBlock = targetBlock;
    }
}
