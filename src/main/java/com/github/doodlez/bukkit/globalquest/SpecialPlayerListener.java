package com.github.doodlez.bukkit.globalquest; /**
 * User: YBogomolov
 * Date: 13.07.11
 * Time: 1:27
 */

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class SpecialPlayerListener extends PlayerListener {
    public static String playerNameToObserve;

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getName().equals("Doodlez")) {
            event.setJoinMessage(null);
            player.setDisplayName(null);
        }

        //super.onPlayerJoin(event);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
