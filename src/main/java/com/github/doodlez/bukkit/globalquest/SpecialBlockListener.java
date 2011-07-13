/**
 * User: YBogomolov
 * Date: 13.07.11
 * Time: 10:14
 */

package com.github.doodlez.bukkit.globalquest;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class SpecialBlockListener extends BlockListener {

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        System.out.print("Player \"" + player.getName() + "\" is placing a block.");
        if (player.getName().equals("")){
            event.setBuild(false);
        }
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        System.out.print("Player \"" + player.getName() + "\" is breaking a block.");
        if (player.getName().equals("")){
            event.setCancelled(true);
        }
    }
}
