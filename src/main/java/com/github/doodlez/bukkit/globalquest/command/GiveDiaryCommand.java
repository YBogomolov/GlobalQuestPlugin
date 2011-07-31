/**
 * User: YBogomolov
 * Date: 30.07.11
 * Time: 16:14
 */

package com.github.doodlez.bukkit.globalquest.command;

import com.github.doodlez.bukkit.globalquest.GlobalQuestPlugin;
import com.github.doodlez.bukkit.globalquest.utilities.Diary;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Handles '/givediary' user command.
 */
public class GiveDiaryCommand implements CommandExecutor {
    /**
     * GIves OP one of Isaak Breen's diaries.
     * @param commandSender Sender of the command.
     * @param command Command text.
     * @param label Command label.
     * @param args Command arguments.
     * @return True, if command executed successfully, and false otherwise.
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player)commandSender;
            if (player.isOp()) {
                if (args.length != 0) {
                    short durability = Short.parseShort(args[0]);

                    if (durability > Diary.totalCount) {
                        player.sendMessage("Cannot find diary number " + durability + ". Please, check the number.");
                        return false;
                    }


                    ItemStack diary = new ItemStack(Material.BOOK, 1, durability);
                    player.getInventory().addItem(diary);
                    return true;
                }
                else
                    return false;
            }
            else
                return false;
        }
        return false;
    }
}
