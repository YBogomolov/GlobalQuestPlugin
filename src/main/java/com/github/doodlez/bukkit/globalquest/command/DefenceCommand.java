/**
 * User: YBogomolov
 * Date: 17.07.11
 * Time: 12:22
 */
package com.github.doodlez.bukkit.globalquest.command;

import com.github.doodlez.bukkit.globalquest.GlobalQuestPlugin;
import com.github.doodlez.bukkit.globalquest.utilities.AirBase;
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

                if (airBase.domeEnabled) {
                    // Disable glass over the airbase:
                    airBase.domeEnabled = false;

                    commandSender.sendMessage("Glass disabled.");

                    AirBase.toggleGlass(player.getServer().getWorld(airBase.worldName), airBase, false);
                }
                else {
                    // Enable glass over the airbase:
                    airBase.domeEnabled = true;

                    commandSender.sendMessage("Glass enabled.");

                    boolean emergency = false;
                    if (args.length != 0)
                        emergency = args[0].equals("emergency");
                    AirBase.toggleGlass(player.getServer().getWorld(airBase.worldName), airBase, emergency);
                }


                return true;
            }
        }
        return false;
    }
}