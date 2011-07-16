/**
 * User: YBogomolov
 * Date: 13.07.11
 * Time: 1:05
 */
package com.github.doodlez.bukkit.globalquest;

import com.github.doodlez.bukkit.globalquest.utilities.AirBase;
import com.github.doodlez.bukkit.globalquest.utilities.LightningPairedBlocks;
import com.github.doodlez.bukkit.globalquest.utilities.LightningRunnable;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

import static org.bukkit.event.Event.Type;

/**
 * Main plugin class.
 */
public class GlobalQuestPlugin extends JavaPlugin {
    // Private fields:
    private final static SpecialPlayerListener playerListener = new SpecialPlayerListener();
    private final static SpecialBlockListener blockListener = new SpecialBlockListener();
    private final static SpecialEntityListener entityListener = new SpecialEntityListener();
    private static HashMap<World, LightningRunnable> lightningTasks = new HashMap<World, LightningRunnable>();

    // Public fields:
    public static boolean isDebugEnabled = false;
    public static String playerNameToObserve;
    public static HashMap<World, AirBase> airBases = new HashMap<World, AirBase>();

    /**
     * Occurs when plugin is disabled (unloaded from Bukkit).
     */
    @Override
    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.print(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled.");
    }

    /**
     * Occurs when plugin is loaded into Bukkit.
     */
    @Override
    public void onEnable() {
        readConfiguration();

        if (isDebugEnabled) {
            System.out.print("World list:");
            for (World world: getServer().getWorlds()){
                System.out.print(world.getName());
            }
        }

        PluginManager manager = getServer().getPluginManager();

        // Player events:
        manager.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
        manager.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
        manager.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Highest, this);
        manager.registerEvent(Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);
        manager.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.High, this);

        // Block events:
        manager.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
        manager.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Normal, this);

        // Entity events:
        manager.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);

        // Strikes lightning at target block if source block is not broken:
        for (World world: airBases.keySet()) {
            AirBase airBase = airBases.get(world);
            this.getServer().getScheduler().scheduleSyncRepeatingTask(this, lightningTasks.get(world), 0, airBase.lightningFrequency);
        }

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.print(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled.");
    }

    /**
     * Reads configuration from GlobalQuestPlugin//config.yml and sets all parameters.
     */
    private void readConfiguration() {
        getConfiguration().load();

        isDebugEnabled = getConfiguration().getBoolean("IsDebugEnabled", false);
        
        playerNameToObserve = getConfiguration().getString("PlayerNameToObserve", "Sinister");

        try {
            int worldCount = getConfiguration().getInt("AirBase.WorldCount", 0);
            for (int worldIndex = 0; worldIndex < worldCount; ++worldIndex) {
                String worldPrefix = "AirBase.World" + worldIndex;
                String worldName = getConfiguration().getString(worldPrefix + ".Name");
                
                World world = getServer().getWorld(worldName);

                AirBase airBase = new AirBase(worldName);

                airBase.lightningFrequency = getConfiguration().getInt(worldPrefix + ".LightningFrequency", 20);

                double airbaseCoordinatesX = getConfiguration().getDouble(worldPrefix + ".X", 0);
                double airbaseCoordinatesY = getConfiguration().getDouble(worldPrefix + ".Y", 0);
                double airbaseCoordinatesZ = getConfiguration().getDouble(worldPrefix + ".Z", 0);
                airBase.airbaseCenterCoordinates = new Location(world, airbaseCoordinatesX, airbaseCoordinatesY, airbaseCoordinatesZ);

                airBase.airbaseRadius = getConfiguration().getInt(worldPrefix + ".Radius", 20);

                airBase.lightningSourceId = getConfiguration().getInt(worldPrefix + ".Lightning.SourceId", 35);

                int lightningBlocksCount  = getConfiguration().getInt(worldPrefix + ".Lightning.BlocksCount", 1);
                for (int index = 0; index < lightningBlocksCount; ++index) {
                    LightningPairedBlocks blocks = new LightningPairedBlocks();

                    double targetX = getConfiguration().getDouble(worldPrefix + ".Lightning.TargetBlock" + index + ".X", 0);
                    double targetY = getConfiguration().getDouble(worldPrefix + ".Lightning.TargetBlock" + index + ".Y", 0);
                    double targetZ = getConfiguration().getDouble(worldPrefix + ".Lightning.TargetBlock" + index + ".Z", 0);
                    Location targetCoordinates = new Location(world, targetX, targetY, targetZ);

                    double sourceX = getConfiguration().getDouble(worldPrefix + ".Lightning.SourceBlock" + index + ".X", 0);
                    double sourceY = getConfiguration().getDouble(worldPrefix + ".Lightning.SourceBlock" + index + ".Y", 0);
                    double sourceZ = getConfiguration().getDouble(worldPrefix + ".Lightning.SourceBlock" + index + ".Z", 0);
                    Location sourceCoordinates = new Location(world, sourceX, sourceY, sourceZ);

                    blocks.setTargetBlock(targetCoordinates);
                    blocks.setSourceBlock(sourceCoordinates);

                    airBase.lightningPairedBlocksList.add(blocks);
                }

                airBases.put(world, airBase);

                LightningRunnable task = new LightningRunnable(world);
                lightningTasks.put(world, task);
            }
        }
        catch (Exception e) {
            System.out.print("Oops! Something happened: " + e.getMessage());
        }

        getConfiguration().save();
    }
}
