/**
 * User: YBogomolov
 * Date: 17.07.11
 * Time: 12:22
 */
package com.github.doodlez.bukkit.globalquest.command;

import com.github.doodlez.bukkit.globalquest.GlobalQuestPlugin;
import com.github.doodlez.bukkit.globalquest.utilities.AirBase;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Class which handles user command '/defence'.
 */
public class DefenceCommand implements CommandExecutor {
    /**
     * Handles user command event.
     * @param commandSender Sender of the commands (most of the time — player).
     * @param command Command text.
     * @param label Command label.
     * @param args Command arguments.
     * @return True, if command executed successfully, and false otherwise.
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player)commandSender;
            if (player.getName().equals("")) {
                AirBase airBase = GlobalQuestPlugin.airBases.get(player.getWorld());
                if (airBase == null)
                    return false;

                if (airBase.glassEnabled) {
                    // Disable glass over the airbase:
                    airBase.glassEnabled = false;

                    commandSender.sendMessage("Glass disabled.");
                }
                else {
                    // Enable glass over the airbase:
                    airBase.glassEnabled = true;

                    commandSender.sendMessage("Glass enabled.");
                }

                toggleGlass(player, airBase);

                return true;
            }
        }
        return false;
    }

    /**
     * Toggles defencive glass dome on and off.
     * @param player Player who issues command.
     * @param airBase AirBase to toggle dome over.
     */
    public static void toggleGlass(Player player, AirBase airBase) {
        Block airBaseCenterBlock = player.getWorld().getBlockAt(airBase.airbaseCenterCoordinates);

        // Center coordinates — just shorter references.
        int x0 = airBaseCenterBlock.getX();
        int y0 = airBaseCenterBlock.getY();
        int z0 = airBaseCenterBlock.getZ();

        if (GlobalQuestPlugin.isDebugEnabled)
            System.out.print("Building sphere: at (" + x0 + "," + y0 + "," + z0 + "), R = " + airBase.domeRadius);

        for (int x = -airBase.domeRadius; x <= airBase.domeRadius; ++x) {
            for (int y = -airBase.domeRadius; y <= airBase.domeRadius; ++y) {
                for (int z = -airBase.domeRadius; z <= airBase.domeRadius; ++z) {
                    int X = x + x0;
                    int Y = y + y0;
                    int Z = z + z0;
                    if (AirBase.blockBelongsToDome(x, y, z, airBase.domeRadius, airBase.domeThickness)) {
                        if (airBase.glassEnabled) {
                            if (player.getWorld().getBlockAt(X, Y, Z ).getType() == Material.AIR) {
                                player.getWorld().getBlockAt(X, Y, Z).setType(Material.GLASS);
                            }
                        }
                        else {
                            for (int dx = -1; dx <= 1; ++dx)
                                for (int dy = -1; dy <= 1; ++dy)
                                    for (int dz = -1; dz <= 1; ++dz)
                                        if (player.getWorld().getBlockAt(X + dx, Y + dy, Z + dz).getType() == Material.GLASS) {
                                            player.getWorld().getBlockAt(X + dx, Y + dy, Z + dz).setType(Material.AIR);
                                        }
                        }
                    }
                }
            }
        }
    }
}