 /**
 * User: YBogomolov
 * Date: 13.07.11
 * Time: 1:05
 */
package com.github.doodlez.bukkit.globalquest;

 import com.github.doodlez.bukkit.globalquest.utilities.AirBase;
 import com.github.doodlez.bukkit.globalquest.utilities.LightningPairedBlocks;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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
    public static AirBase airBase;

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
        System.out.print("World list:");
        for (World world: getServer().getWorlds()){
            System.out.print(world.getName());
        }

        theWorld = this.getServer().getWorld("world");
        readConfiguration();

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
                boolean previousHasStorm = theWorld.hasStorm();
                boolean allBlocksDestroyed = true;
                for (LightningPairedBlocks blocks: AirBase.lightningPairedBlocksList) {
                    Block block = theWorld.getBlockAt(blocks.getSourceBlock());
                    if (block.getTypeId() == AirBase.lightningSourceId) {
                        allBlocksDestroyed = false;
                        theWorld.setStorm(true);
                        if (isDebugEnabled) {
                            System.out.print("Lightning strike at " + blocks.getTargetBlock().getX() + ", " +
                                                                      blocks.getTargetBlock().getY() + ", " +
                                                                      blocks.getTargetBlock().getZ());
                        }
                        theWorld.strikeLightning(blocks.getTargetBlock());
                    }
                }

                if (allBlocksDestroyed)
                    theWorld.setStorm(previousHasStorm);
            }
        };
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, task, 0, AirBase.lightningFrequency);

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

        double airbaseCoordinatesX = getConfiguration().getDouble("GQP.AirBase.X", 0);
        double airbaseCoordinatesY = getConfiguration().getDouble("GQP.AirBase.Y", 0);
        double airbaseCoordinatesZ = getConfiguration().getDouble("GQP.AirBase.Z", 0);
        AirBase.airbaseCenterCoordinates = new Location(theWorld, airbaseCoordinatesX, airbaseCoordinatesY, airbaseCoordinatesZ);
        AirBase.airbaseRadius = getConfiguration().getInt("GQP.AirBase.Radius", 20);

        int lightningBlocksCount = getConfiguration().getInt("GQP.AirBase.Lightning.BlocksCount", 1);
        AirBase.lightningFrequency = getConfiguration().getInt("GQP.AirBase.Lightning.Frequency", 20);
        AirBase.lightningSourceId = getConfiguration().getInt("GQP.AirBase.Lightning.SourceId", 35);
        for (int index = 0; index < lightningBlocksCount; ++index) {
            LightningPairedBlocks blocks = new LightningPairedBlocks();

            double targetX = getConfiguration().getDouble("GQP.AirBase.Lightning.TargetBlock" + index + "X", 0);
            double targetY = getConfiguration().getDouble("GQP.AirBase.Lightning.TargetBlock" + index + "Y", 0);
            double targetZ = getConfiguration().getDouble("GQP.AirBase.Lightning.TargetBlock" + index + "Z", 0);
            Location targetCoordinates = new Location(theWorld, targetX, targetY, targetZ);

            double sourceX = getConfiguration().getDouble("GQP.AirBase.Lightning.SourceBlock" + index + "X", 0);
            double sourceY = getConfiguration().getDouble("GQP.AirBase.Lightning.SourceBlock" + index + "Y", 0);
            double sourceZ = getConfiguration().getDouble("GQP.AirBase.Lightning.SourceBlock" + index + "Z", 0);
            Location sourceCoordinates = new Location(theWorld, sourceX, sourceY, sourceZ);

            blocks.setTargetBlock(targetCoordinates);
            blocks.setSourceBlock(sourceCoordinates);

            AirBase.lightningPairedBlocksList.add(blocks);
        }
        
        getConfiguration().save();
    }
}
