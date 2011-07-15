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
    private Location sourceBlock;
    private Location targetBlock;

    public Location getSourceBlock() {
        return sourceBlock;
    }

    public void setSourceBlock(Location sourceBlock) {
        this.sourceBlock = sourceBlock;
    }

    public Location getTargetBlock() {
        return targetBlock;
    }

    public void setTargetBlock(Location targetBlock) {
        this.targetBlock = targetBlock;
    }
}
