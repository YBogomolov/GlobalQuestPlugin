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
 * Class which handles user command '/dome'.
 */
public class DomeCommand implements CommandExecutor {
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
                System.out.println(player.getWorld().getName());
                AirBase airBase = GlobalQuestPlugin.airBases.get(player.getWorld());

                if (airBase == null)
                    return false;
                
                boolean emergency = false;
                if (args.length != 0)
                    emergency = args[0].equals("emergency");

                if (airBase.domeEnabled && !emergency) {
                    // Disable glass over the airbase:
                    airBase.domeEnabled = false;

                    commandSender.sendMessage("Dome disabled.");

                    AirBase.toggleGlass(player.getServer().getWorld(airBase.worldName), airBase, false);
                }
                else {
                    // Enable glass over the airbase:
                    airBase.domeEnabled = true;

                    if (emergency)
                        commandSender.sendMessage("Dome enabled in emergency mode.");
                    else
                        commandSender.sendMessage("Dome enabled.");

                    AirBase.toggleGlass(player.getServer().getWorld(airBase.worldName), airBase, emergency);
                }
                return true;
            }
        }
        return false;
    }
}