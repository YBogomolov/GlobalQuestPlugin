/**
 * User: YBogomolov
 * Date: 13.07.11
 * Time: 10:14
 */

package com.github.doodlez.bukkit.globalquest;

import com.github.doodlez.bukkit.globalquest.utilities.Coordinates;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class SpecialBlockListener extends BlockListener {
    /**
     * Handles BLOCK_PLACE event. Prohibits Isaak Breen to place blocks.
     * @param event BlockPlaceEvent.
     */
    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        System.out.print("Player \"" + player.getName() + "\" is placing a block.");
        if (player.getName().equals("")){
            Coordinates chunkCenterCoord = Coordinates.GetChunkCenterCoordinates(event.getBlockPlaced());
            System.out.print("Chunk coord: " + chunkCenterCoord.X + ", " + chunkCenterCoord.Z);

            if ((GlobalQuestPlugin.airbaseCoordinates.X != chunkCenterCoord.X)
                && (GlobalQuestPlugin.airbaseCoordinates.Z != chunkCenterCoord.Z)) {
                event.setBuild(false);
            }
            else {
                System.out.print("He's on his base, thus allowed to build.");
                event.setBuild(true);
            }
        }
    }

    /**
     * Handles BLOCK_BREAK event. Prohibites Isaak Breen to break blocks.
     * @param event BlockBreakEvent.
     */
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        System.out.print("Player \"" + player.getName() + "\" is breaking a block.");
        if (player.getName().equals("")) {
            Coordinates chunkCenterCoord = Coordinates.GetChunkCenterCoordinates(event.getBlock());

            if ((GlobalQuestPlugin.airbaseCoordinates.X != chunkCenterCoord.X)
                && (GlobalQuestPlugin.airbaseCoordinates.Z != chunkCenterCoord.Z)) {
                event.setCancelled(true);
            }
            else {
                System.out.print("He's on his base, thus allowed to break.");
                event.setCancelled(false);
            }
        }
    }
}
