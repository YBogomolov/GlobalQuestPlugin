/**
 * User: YBogomolov
 * Date: 13.07.11
 * Time: 1:05
 */
package com.github.doodlez.bukkit.globalquest;

import com.github.doodlez.bukkit.globalquest.command.DomeCommand;
import com.github.doodlez.bukkit.globalquest.command.GiveDiaryCommand;
import com.github.doodlez.bukkit.globalquest.listeners.SpecialBlockListener;
import com.github.doodlez.bukkit.globalquest.listeners.SpecialEntityListener;
import com.github.doodlez.bukkit.globalquest.listeners.SpecialPlayerListener;
import com.github.doodlez.bukkit.globalquest.listeners.SpecialServerListener;
import com.github.doodlez.bukkit.globalquest.utilities.AirBase;
import com.github.doodlez.bukkit.globalquest.utilities.Diary;
import com.github.doodlez.bukkit.globalquest.utilities.LightningPairedBlocks;
import com.github.doodlez.bukkit.globalquest.utilities.LightningRunnable;
import net.minecraft.server.CraftingRecipe;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.ConfigurationNode;
import sun.security.krb5.Config;

import javax.xml.transform.sax.SAXTransformerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.event.Event.Type;

/**
 * Main plugin class.
 */
public class GlobalQuestPlugin extends JavaPlugin {
    // Private fields:
    private final static SpecialPlayerListener playerListener = new SpecialPlayerListener();
    private final static SpecialBlockListener blockListener = new SpecialBlockListener();
    private final static SpecialEntityListener entityListener = new SpecialEntityListener();
    private final static SpecialServerListener serverListener = new SpecialServerListener();
    private static HashMap<World, LightningRunnable> lightningTasks = new HashMap<World, LightningRunnable>();

    // Public fields:
    public static boolean isDebugEnabled = false;
    public static String playerNameToObserve;
    public static int playerBaseDamage;
    public static int playerDamageModifier;
    public static boolean playerInvincibleToHisArrows;
    public static int jetpackId;
    public static HashMap<World, AirBase> airBases = new HashMap<World, AirBase>();

    public static ArrayList<String> blockedRecipes = new ArrayList<String>();
    public static HashMap<String, CraftingRecipe> backupRecipes = new HashMap<String, CraftingRecipe>();
    public static HashMap<Integer, Diary> diaries = new HashMap<Integer, Diary>();

    /**
     * Occurs when plugin is disabled (unloaded from Bukkit).
     */
    @Override
    public void onDisable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.print(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled.");
        saveConfiguration();
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
        manager.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.High, this);
        manager.registerEvent(Type.EXPLOSION_PRIME, entityListener, Priority.Normal, this);

        // Plugin events:
        manager.registerEvent(Type.PLUGIN_ENABLE, serverListener, Priority.Highest, this);

        // Commands:
        getCommand("dome").setExecutor(new DomeCommand());
        getCommand("givediary").setExecutor(new GiveDiaryCommand());

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

        List<Object> blockedRecipes = getConfiguration().getList("BlockedRecipes");

        for (Object recipe : blockedRecipes) {
            GlobalQuestPlugin.blockedRecipes.add(recipe.toString());
        }

        int diaryCount = getConfiguration().getInt("Diaries.Count", 0);

        for (int index = 0; index < diaryCount; ++index) {
            Diary diary = new Diary();
            List<Object> diaryRaw = getConfiguration().getList("Diaries.Diary" + diary.number + ".Text");

            for (Object diaryPart : diaryRaw) {
                diary.text.add(diaryPart.toString());
            }

            List<Object> recipes = getConfiguration().getList("Diaries.Diary" + diary.number + ".UnblockedRecipes");
            if (recipes != null)
                for (Object recipe : recipes) {
                    if (recipe != null)
                        diary.unblockedRecipes.add(recipe.toString());
                }
            diaries.put(diary.number, diary);
        }
        
        playerNameToObserve = getConfiguration().getString("PlayerNameToObserve", "Sinister");
        playerBaseDamage = getConfiguration().getInt("PlayerBaseDamage", 0);
        playerDamageModifier = getConfiguration().getInt("PlayerDamageModifier", 0);
        playerInvincibleToHisArrows = getConfiguration().getBoolean("PlayerInvincibleToHisArrows", false);
        jetpackId = getConfiguration().getInt("JetpackId", 0);

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

                airBase.domeRadius = getConfiguration().getInt(worldPrefix + ".DomeRadius", 20);
                airBase.domeThickness = getConfiguration().getDouble(worldPrefix + ".DomeThickness", 2);

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

    /**
     * Saves configuration in order to save game progress.
     */
    private void saveConfiguration() {
        getConfiguration().load();
        
        getConfiguration().setProperty("IsDebugEnabled", GlobalQuestPlugin.isDebugEnabled);

        getConfiguration().setProperty("BlockedRecipes", blockedRecipes);

        getConfiguration().setProperty("Diaries.Count", Diary.totalCount);

        for (int index = 0; index < Diary.totalCount; ++index) {
            Diary diary = diaries.get(index + 1);
            String prefix = "Diaries.Diary" + (index+1);
            getConfiguration().setProperty(prefix + ".Text", diary.text);
            getConfiguration().setProperty(prefix + ".UnblockedRecipes", diary.unblockedRecipes);
        }

        getConfiguration().setProperty("PlayerNameToObserve", playerNameToObserve);
        getConfiguration().setProperty("PlayerBaseDamage", playerBaseDamage);
        getConfiguration().setProperty("PlayerDamageModifier", playerDamageModifier);
        getConfiguration().setProperty("PlayerInvincibleToHisArrows", playerInvincibleToHisArrows);
        getConfiguration().setProperty("JetpackID", jetpackId);

        getConfiguration().setProperty("AirBase.WorldCount", airBases.size());

        int worldIndex = 0;
        for (AirBase airBase : airBases.values()) {
            String prefix = "AirBase.World" + worldIndex;
            getConfiguration().setProperty(prefix + ".Name", airBase.worldName);
            getConfiguration().setProperty(prefix + ".LightningFrequency", airBase.lightningFrequency);
            getConfiguration().setProperty(prefix + ".X", airBase.airbaseCenterCoordinates.getX());
            getConfiguration().setProperty(prefix + ".Y", airBase.airbaseCenterCoordinates.getY());
            getConfiguration().setProperty(prefix + ".Z", airBase.airbaseCenterCoordinates.getZ());
            getConfiguration().setProperty(prefix + ".DomeRadius", airBase.domeRadius);
            getConfiguration().setProperty(prefix + ".DomeThickness", airBase.domeThickness);
            getConfiguration().setProperty(prefix + ".Lightning.SourceId", airBase.lightningSourceId);
            getConfiguration().setProperty(prefix + ".Lightning.BlocksCount", airBase.lightningPairedBlocksList.size());

            prefix = prefix + ".Lightning";

            int blockId = 0;
            for (LightningPairedBlocks blocks : airBase.lightningPairedBlocksList) {
                String srcPrefix = prefix + ".SourceBlock" + blockId;
                String trgPrefix = prefix + ".TargetBlock" + blockId;

                getConfiguration().setProperty(srcPrefix + ".X", blocks.getSourceBlock().getX());
                getConfiguration().setProperty(srcPrefix + ".Y", blocks.getSourceBlock().getY());
                getConfiguration().setProperty(srcPrefix + ".Z", blocks.getSourceBlock().getZ());

                getConfiguration().setProperty(trgPrefix + ".X", blocks.getTargetBlock().getX());
                getConfiguration().setProperty(trgPrefix + ".Y", blocks.getTargetBlock().getY());
                getConfiguration().setProperty(trgPrefix + ".Z", blocks.getTargetBlock().getZ());

                blockId++;
            }

            worldIndex++;
        }

        getConfiguration().save();
    }
}
