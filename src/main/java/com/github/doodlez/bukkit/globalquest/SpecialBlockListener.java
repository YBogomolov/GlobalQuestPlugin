/**
 * User: YBogomolov
 * Date: 13.07.11
 * Time: 10:14
 */
package com.github.doodlez.bukkit.globalquest;

import com.github.doodlez.bukkit.globalquest.utilities.AirBase;
import org.bukkit.Material;
import org.bukkit.block.Block;
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

        if (GlobalQuestPlugin.isDebugEnabled)
            System.out.print("Player \"" + player.getName() + "\" is placing a block.");
        if (player.getName().equals("")){
            AirBase airBase = GlobalQuestPlugin.airBases.get(event.getBlockPlaced().getWorld());
            if (AirBase.blockBelongsToAirbase(event.getBlockPlaced(), airBase)) {
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

        if (GlobalQuestPlugin.isDebugEnabled)
            System.out.print("Player \"" + player.getName() + "\" is breaking a block.");
        if (player.getName().equals("")) {
            AirBase airBase = GlobalQuestPlugin.airBases.get(event.getBlock().getWorld());
            if (AirBase.blockBelongsToAirbase(event.getBlock(), airBase)) {
                if (GlobalQuestPlugin.isDebugEnabled)
                    System.out.print("He's on his base, thus allowed to break.");
                event.setCancelled(false);
            }
            else {
                event.setCancelled(true);
            }
        }
        else {
            // Need to check — if some player tries to break glass dome while Isaak offline.
            boolean isaakOnline = false;
            for (Player playerToTest: player.getWorld().getPlayers()) {
                if (playerToTest.getName().equals("")) {
                    isaakOnline = true;
                    break;
                }
            }

            if (!isaakOnline) {
                Block block = event.getBlock();
                AirBase airBase = GlobalQuestPlugin.airBases.get(player.getWorld());
                if (block.getType().equals(Material.GLASS)) {
                    if (AirBase.blockBelongsToDome(block.getX(), block.getY(), block.getZ(), airBase.domeRadius, airBase.domeThickness)) {
                        // We shouldn't allow this!
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
