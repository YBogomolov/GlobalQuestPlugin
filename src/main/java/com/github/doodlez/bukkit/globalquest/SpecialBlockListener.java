/**
 * User: YBogomolov
 * Date: 13.07.11
 * Time: 10:14
 */

package com.github.doodlez.bukkit.globalquest;

import com.github.doodlez.bukkit.globalquest.utilities.AirBase;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Class that handles Isaak Breen's actions against blocks — destruction, placement, etc.
 */
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
            if (AirBase.blockBelongsToAirbase(event.getBlockPlaced())) {
                if (GlobalQuestPlugin.isDebugEnabled)
                    System.out.print("He's on his base, thus allowed to build.");
                event.setBuild(true);
            }
            else {
                event.setBuild(false);
            }
        }
    }

    /**
     * Handles BLOCK_BREAK event. Prohibits Isaak Breen to break blocks.
     * @param event BlockBreakEvent.
     */
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        System.out.print("Player \"" + player.getName() + "\" is breaking a block.");
        if (player.getName().equals("")) {
            if (AirBase.blockBelongsToAirbase(event.getBlock())) {
                if (GlobalQuestPlugin.isDebugEnabled)
                    System.out.print("He's on his base, thus allowed to break.");
                event.setCancelled(false);
            }
            else {
                event.setCancelled(true);
            }
        }
    }
}
