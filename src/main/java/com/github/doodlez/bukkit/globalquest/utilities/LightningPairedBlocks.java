/**
 * User: YBogomolov
 * Date: 15.07.11
 * Time: 13:46
 */

package com.github.doodlez.bukkit.globalquest.utilities;

/**
 * Represents lightning paired blocks — is source is not broken, target is hit by lightning.
 */
public class LightningPairedBlocks {
    private Coordinates sourceBlock;
    private Coordinates targetBlock;

    public LightningPairedBlocks() {
        sourceBlock = new Coordinates();
        targetBlock = new Coordinates();
    }

    public Coordinates getSourceBlock() {
        return sourceBlock;
    }

    public void setSourceBlock(Coordinates sourceBlock) {
        this.sourceBlock = sourceBlock;
    }

    public Coordinates getTargetBlock() {
        return targetBlock;
    }

    public void setTargetBlock(Coordinates targetBlock) {
        this.targetBlock = targetBlock;
    }
}
