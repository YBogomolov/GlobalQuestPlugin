package com.github.doodlez.bukkit.globalquest; /**
 * User: YBogomolov
 * Date: 13.07.11
 * Time: 1:05
 */

import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.event.Event.Type;

public class GlobalQuestPlugin extends JavaPlugin {
    private final static SpecialPlayerListener playerListener = new SpecialPlayerListener();

    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.print(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled.");
    }

    public void onEnable() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvent(Type.PLAYER_LOGIN, playerListener, Priority.Normal, this);

        readConfiguration();

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.print(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled.");
    }

    /**
     * Reads configuration from com.github.doodlez.bukkit.globalquest.GlobalQuestPlugin//config.yml and sets all parameters.
     */
    private void readConfiguration() {
        getConfiguration().load();
        //SpecialPlayerListener.playerNameToObserve = getConfiguration().getString("GQP.PlayerNameToObserve", "Sinister");
        getConfiguration().save();
    }
}
