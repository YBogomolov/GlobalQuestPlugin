/**
 * User: YBogomolov
 * Date: 13.07.11
 * Time: 1:27
 */
package com.github.doodlez.bukkit.globalquest.listeners;

import com.github.doodlez.bukkit.globalquest.GlobalQuestPlugin;
import com.github.doodlez.bukkit.globalquest.utilities.AirBase;
import com.github.doodlez.bukkit.globalquest.utilities.Diary;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Class that handles special mod-like player Isaak Breen, needed for Global Quest.
 */
public class SpecialPlayerListener extends PlayerListener {
    /**
     * Handles PLAYER_JOIN event. If player is special (Isaak Breen),
     * then make him and his actions invisible to others.
     *
     * @param event Player join event.
     */
    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.getName().equals(GlobalQuestPlugin.playerNameToObserve)) {
            System.out.print(player.getName() + " joined the game.");

            // Let's make Isaak Breen invisible to others — i.e. no name over the head, no name in chat.
            System.out.print("Let's make him disappear...");
            event.setJoinMessage(null);
            player.setDisplayName("");

            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            entityPlayer.name = "";

            for (Player everyPlayer : Bukkit.getServer().getOnlinePlayers()) {
                if (everyPlayer != player) {
                    ((CraftPlayer) everyPlayer).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(entityPlayer));
                }
            }

            GiveDefaultInventoryTo(player);
        }
    }

    /**
     * Gives specified player a bow and infinite stack of arrows.
     * @param player Player with such great responsibility (e.g., Isaak Breen).
     */
    private void GiveDefaultInventoryTo(Player player) {
        // He should have infinite arrows and a bow to defend himself.
        System.out.print("Let's give him infinite arrows, a bow and a diamond sword.");
        PlayerInventory inventory = player.getInventory();

        inventory.remove(Material.ARROW);
        inventory.remove(Material.BOW);
        inventory.remove(Material.DIAMOND_SWORD);

        ItemStack arrowStack = new ItemStack(Material.ARROW, 64);
        ItemStack bowStack = new ItemStack(Material.BOW, 1);
        ItemStack swordStack = new ItemStack(Material.DIAMOND_SWORD, 1);

        ItemStack paper1 = new ItemStack(Material.PAPER, 1);
        paper1.setDurability((short)1);
        ItemStack paper2 = new ItemStack(Material.PAPER, 1);
        paper2.setDurability((short)2);

        inventory.addItem(bowStack);
        inventory.addItem(arrowStack);
        inventory.addItem(swordStack);
        inventory.addItem(paper1);
        inventory.addItem(paper2);
    }

    /**
     * Handles PLAYER_QUIT event. Is players is special, then
     * disable his quit message in chat.
     * Also puts on defence dome over all airbases.
     * @param event Player quit event.
     */
    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (player.getName().equals("")) {
            for (AirBase airBase: GlobalQuestPlugin.airBases.values()) {
                if (!airBase.domeEnabled) {
                    if (GlobalQuestPlugin.isDebugEnabled)
                        System.out.print("Warning! Dome was disabled. Enabling!");
                    airBase.domeEnabled = true;
                    AirBase.toggleGlass(player.getServer().getWorld(airBase.worldName), airBase, true);
                }
                else {
                    if (GlobalQuestPlugin.isDebugEnabled)
                        System.out.print("Warning! Dome is in glass mode. Enforcing!");
                    AirBase.toggleGlass(player.getServer().getWorld(airBase.worldName), airBase, false); // Regular defence disabling...
                    AirBase.toggleGlass(player.getServer().getWorld(airBase.worldName), airBase, true); // ...and re-enabling its with OBSIDIAN!
                }
            }
            System.out.print(player.getName() + " left the game.");
            event.setQuitMessage(null);

            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            entityPlayer.name = GlobalQuestPlugin.playerNameToObserve;
        }
    }

    /**
     * Handles PLAYER_INTERACT event. Sets his arrows to infinite.
     * @param event Player interact event.
     */
    @SuppressWarnings({"deprecation"})
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // If this is Isaak Breen:
        if (player.getName().equals("")) {
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR)
                || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

                if (GlobalQuestPlugin.isDebugEnabled) {
                    System.out.print("Coordinates: " + player.getTargetBlock(null, 500).getX() + ", " +
                                                       player.getTargetBlock(null, 500).getY() + ", " +
                                                       player.getTargetBlock(null, 500).getZ());
                }

                if (player.getItemInHand().getType() == Material.BOW) {
                    ItemStack inventory[] = player.getInventory().getContents();
                    for (ItemStack anInventory : inventory) {
                        if (anInventory != null && anInventory.getTypeId() == 262) {
                            anInventory.setAmount(64);
                            player.getInventory().setContents(inventory);
                            player.updateInventory();
                            return;
                        }
                    }
                }
            }
        }
        else {
            // This means we have general player.
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR)
                || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

                ItemStack itemInHand = player.getItemInHand();

                if (itemInHand.getType() == Material.PAPER) {
                    int diaryNumber = itemInHand.getDurability();

                    Diary diary = GlobalQuestPlugin.diaries.get(diaryNumber);

                    player.sendMessage(diary.text);
                    for (String recipeName : diary.unblockedRecipes) {
                        player.sendMessage(recipeName + " was unlocked.");
                        UnlockRecipe(recipeName);
                    }
                    diary.unblockedRecipes.clear();
                }
            }
        }
    }

    /**
     * Unlocks recipe with given name.
     * @param recipeName Recipe name to unlock.
     */
    private void UnlockRecipe(String recipeName) {
        CraftingManager craftingManager = CraftingManager.getInstance();
        
        List<CraftingRecipe> craftList = craftingManager.b();
        List<CraftingRecipe> editedList = new ArrayList<CraftingRecipe>();

        editedList.addAll(craftList);

        CraftingRecipe recipe = GlobalQuestPlugin.backupRecipes.get(recipeName);

        if (!editedList.contains(recipe)) {
            System.out.print("Adding " + recipeName + " to allowed recipes.");
            editedList.add(recipe);
            GlobalQuestPlugin.blockedRecipes.remove(recipeName);
        }

        try {
            Diary.setPrivateValue(CraftingManager.class, craftingManager, "b", editedList);
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles PLAYER_RESPAWN event. Gives Isaak Breen infinite arrow and a bow.
     * @param event PlayerRespawnEvent.
     */
    @Override
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (player.getName().equals("")) {
            GiveDefaultInventoryTo(player);
        }
    }

    /**
     * Handles PLAYER_CHAT event. Allows Isaak Breen only commands (i.e., messages, which start with '/').
     * All other players can chat freely.
     * @param event PlayerChatEvent.
     */
    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();

        Set<Player> recipients = event.getRecipients();

        Player playerToRemove = null;
        for (Player chatPlayer: recipients) {
            if (chatPlayer.getName().equals("")) {
                playerToRemove = chatPlayer;
                break;
            }
        }
        
        if (playerToRemove != null)
            recipients.remove(playerToRemove);

        if (player.getName().equals("")) {
            if (event.getMessage().charAt(0) != '/') {
                event.setCancelled(true);
            }
        }
    }
}
