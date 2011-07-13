 /**
 * User: YBogomolov
 * Date: 13.07.11
 * Time: 1:05
 */
package com.github.doodlez.bukkit.globalquest;
 
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.event.Event.Type;

/**
 * Main plugin class.
 */
public class GlobalQuestPlugin extends JavaPlugin {
    private final static SpecialPlayerListener playerListener = new SpecialPlayerListener();
    private final static SpecialBlockListener blockListener = new SpecialBlockListener();
    public static String playerNameToObserve;

    @Override
    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.print(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled.");
    }

    @Override
    public void onEnable() {
        PluginManager manager = getServer().getPluginManager();

        // Player events:
        manager.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        manager.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        manager.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Highest, this);

        // Block events:
        manager.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
        manager.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Normal, this);

        readConfiguration();

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.print(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled.");
    }

    /**
     * Reads configuration from GlobalQuestPlugin//config.yml and sets all parameters.
     */
    private void readConfiguration() {
        getConfiguration().load();
        playerNameToObserve = getConfiguration().getString("GQP.PlayerNameToObserve", "Sinister");
        getConfiguration().save();
    }
}
