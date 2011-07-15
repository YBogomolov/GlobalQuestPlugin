 /**
 * User: YBogomolov
 * Date: 13.07.11
 * Time: 1:05
 */
package com.github.doodlez.bukkit.globalquest;

 import com.github.doodlez.bukkit.globalquest.utilities.Coordinates;
import com.github.doodlez.bukkit.globalquest.utilities.LightningPairedBlocks;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

import static org.bukkit.event.Event.Type;

/**
 * Main plugin class.
 */
public class GlobalQuestPlugin extends JavaPlugin {
    // Private fields:
    private final static SpecialPlayerListener playerListener = new SpecialPlayerListener();
    private final static SpecialBlockListener blockListener = new SpecialBlockListener();
    private final static SpecialEntityListener entityListener = new SpecialEntityListener();
    private static World theWorld;

    // Public fields:
    public static boolean isDebugEnabled = false;
    public static String playerNameToObserve;
    public static Coordinates airbaseCoordinates = new Coordinates();
    public static ArrayList<LightningPairedBlocks> lightningPairedBlocksList = new ArrayList<LightningPairedBlocks>();
    public static int lightningFrequency;
    public static int lightningSourceId;

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

        System.out.print("World list:");
        for (World world: getServer().getWorlds()){
            System.out.print(world.getName());
        }

        theWorld = this.getServer().getWorld("world");

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
        Runnable task = new Runnable() {
            public void run() {
                boolean allBlocksDestroyed = true;
                for (LightningPairedBlocks blocks: lightningPairedBlocksList) {
                    Block block = theWorld.getBlockAt(blocks.getSourceBlock().X,
                                                      blocks.getSourceBlock().Y,
                                                      blocks.getSourceBlock().Z);
                    if (block.getTypeId() == lightningSourceId) {
                        allBlocksDestroyed = false;
                        theWorld.setStorm(true);
                        if (isDebugEnabled) {
                            System.out.print("Lightning strike at " + blocks.getTargetBlock().X + ", " +
                                                                      blocks.getTargetBlock().Y + ", " +
                                                                      blocks.getTargetBlock().Z);
                        }
                        theWorld.strikeLightning(new Location(theWorld, blocks.getTargetBlock().X,
                                                                        blocks.getTargetBlock().Y,
                                                                        blocks.getTargetBlock().Z));
                    }
                }

                if (allBlocksDestroyed)
                    theWorld.setStorm(false);
            }
        };
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, task, 0, lightningFrequency);

        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.print(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled.");
    }

    /**
     * Reads configuration from GlobalQuestPlugin//config.yml and sets all parameters.
     */
    private void readConfiguration() {
        getConfiguration().load();

        isDebugEnabled = getConfiguration().getBoolean("GQP.IsDebugEnabled", false);
        playerNameToObserve = getConfiguration().getString("GQP.PlayerNameToObserve", "Sinister");

        airbaseCoordinates.X = getConfiguration().getInt("GQP.AirBase.X", 0);
        airbaseCoordinates.Y = getConfiguration().getInt("GQP.AirBase.Y", 0);
        airbaseCoordinates.Z = getConfiguration().getInt("GQP.AirBase.Z", 0);

        int lightningBlocksCount = getConfiguration().getInt("GQP.AirBase.Lightning.BlocksCount", 1);
        lightningFrequency = getConfiguration().getInt("GQP.AirBase.Lightning.Frequency", 20);
        lightningSourceId = getConfiguration().getInt("GQP.AirBase.Lightning.SourceId", 35);
        for (int index = 0; index < lightningBlocksCount; ++index) {
            LightningPairedBlocks blocks = new LightningPairedBlocks();

            Coordinates targetCoordinates = new Coordinates();
            targetCoordinates.X = getConfiguration().getInt("GQP.AirBase.Lightning.TargetBlock" + index + "X", 0);
            targetCoordinates.Y = getConfiguration().getInt("GQP.AirBase.Lightning.TargetBlock" + index + "Y", 0);
            targetCoordinates.Z = getConfiguration().getInt("GQP.AirBase.Lightning.TargetBlock" + index + "Z", 0);

            Coordinates sourceCoordinates = new Coordinates();
            sourceCoordinates.X = getConfiguration().getInt("GQP.AirBase.Lightning.SourceBlock" + index + "X", 0);
            sourceCoordinates.Y = getConfiguration().getInt("GQP.AirBase.Lightning.SourceBlock" + index + "Y", 0);
            sourceCoordinates.Z = getConfiguration().getInt("GQP.AirBase.Lightning.SourceBlock" + index + "Z", 0);

            blocks.setTargetBlock(targetCoordinates);
            blocks.setSourceBlock(sourceCoordinates);

            lightningPairedBlocksList.add(blocks);
        }
        
        getConfiguration().save();
    }
}
