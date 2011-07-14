 /**
 * User: YBogomolov
 * Date: 13.07.11
 * Time: 1:27
 */
package com.github.doodlez.bukkit.globalquest;

 import net.minecraft.server.EntityPlayer;
 import net.minecraft.server.Packet20NamedEntitySpawn;
 import org.bukkit.Bukkit;
 import org.bukkit.Material;
 import org.bukkit.craftbukkit.entity.CraftPlayer;
 import org.bukkit.entity.Player;
 import org.bukkit.event.block.Action;
 import org.bukkit.event.player.PlayerInteractEvent;
 import org.bukkit.event.player.PlayerJoinEvent;
 import org.bukkit.event.player.PlayerListener;
 import org.bukkit.event.player.PlayerQuitEvent;
 import org.bukkit.inventory.ItemStack;
 import org.bukkit.inventory.PlayerInventory;

 /**
 * Class tha handles special mod-like player, needed for Global Quest.
 */
public class SpecialPlayerListener extends PlayerListener {
    /**
     * Handles PLAYER_JOIN event. If player is special (Herobrine),
     * then make him and his actions invisible to others.
     * @param event Player join event.
     */
    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.getName().equals(GlobalQuestPlugin.playerNameToObserve)) {
            System.out.print(player.getName() + " joined the game.");

            // Let's make Herobrine invisible to others — i.e. no name over the head, no name in chat.
            System.out.print("Let's make him disappear...");
            event.setJoinMessage(null);
            player.setDisplayName("");

            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            entityPlayer.name = "";

            for(Player everyPlayer : Bukkit.getServer().getOnlinePlayers()){
                if(everyPlayer != player){
                    ((CraftPlayer) everyPlayer).getHandle().netServerHandler.sendPacket(new Packet20NamedEntitySpawn(entityPlayer));
                }
            }

            // He sould have infinite arrows and a bow to defend himself.
            System.out.print("Let's give him infinite arrows and a bow.");
            PlayerInventory inventory = player.getInventory();

            inventory.remove(Material.ARROW);
            inventory.remove(Material.BOW);

            ItemStack arrowStack = new ItemStack(Material.ARROW, 64);
            ItemStack bowStack = new ItemStack(Material.BOW, 1);
            inventory.addItem(bowStack);
            inventory.addItem(arrowStack);
        }
    }

    /**
     * Handles PLAYER_QUIT event. Is players is special, then
     * disable his quit message in chat.
     * @param event Player quit event.
     */
    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (player.getName().equals("")) {
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
	public void onPlayerInteract(PlayerInteractEvent event)
	{
        Player player = event.getPlayer();
        if (player.getName().equals("")) {
            if(event.getAction().equals(Action.RIGHT_CLICK_AIR)
               || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if(player.getItemInHand().getTypeId() == 261) {
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
	}

}
