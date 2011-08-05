/**
 * User: YBogomolov
 * Date: 14.07.11
 * Time: 13:17
 */

package com.github.doodlez.bukkit.globalquest.listeners;

import com.github.doodlez.bukkit.globalquest.GlobalQuestPlugin;
import com.github.doodlez.bukkit.globalquest.utilities.AirBase;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

/**
 * Class that handles Entity-to-Entity interactions and applies appropriate treats.
 */
public class SpecialEntityListener extends EntityListener {
    /**
     * Handles ENTITY_DAMAGE event. Sets appropriate damage to Isaak Breen.
     * @param event EntityDamageEvent.
     */
    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        // Only process when both damager and damagee are Entities.
        if (event instanceof EntityDamageByEntityEvent) {
            // Let's set appropriate damage values for Isaak Breen if he attacks player
            // or he is attacked by player.
            if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;

                //Check to see if the damager and damaged are players
                if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
                    Player damager = (Player)e.getDamager();
                    Player damagee = (Player)e.getEntity();

                    final int damage = e.getDamage();
                    int playersOnline = damager.getServer().getOnlinePlayers().length - 1; // Isaak himself doesn't count.

                    // Now we have options:
                    // 0. If he is stupid and firing arrows at himself — do regular damage or no damage  at all:
                    if ((damager.getName().equals(damagee.getName())) && (damager.getName().equals(""))) {
                        if (GlobalQuestPlugin.playerInvincibleToHisArrows)
                            e.setDamage(0);
                        else
                            e.setDamage(damage);
                        return;
                    }

                    // 1. If Isaak Breen is damager, he should deal DOUBLE damage:
                    if (damager.getName().equals("")) {
                        // Let's make damage dynamical, depending on number of players online:
                        Material weapon = damager.getItemInHand().getType();

                        double damageModifier = 1.0;

                        switch (weapon) {
                            case BOW: {
                                switch (playersOnline) {
                                    case 0:
                                    case 1:
                                        damageModifier = 1.10;
                                        break;
                                    case 2:
                                        damageModifier = 1.2;
                                        break;
                                    case 3:
                                        damageModifier = 1.3;
                                        break;
                                    case 4:
                                        damageModifier = 1.4;
                                        break;
                                    default:
                                        damageModifier = 1.5;
                                        break;
                                }
                                break;
                            }
                            case DIAMOND_SWORD:
                            case IRON_SWORD:
                            case GOLD_SWORD:
                            case STONE_SWORD:
                            case WOOD_SWORD: {
                                switch (playersOnline) {
                                    case 0:
                                    case 1:
                                        damageModifier = 2.0;
                                        break;
                                    case 2:
                                        damageModifier = 2.25;
                                        break;
                                    case 3:
                                        damageModifier = 2.4;
                                        break;
                                    case 4:
                                        damageModifier = 2.5;
                                        break;
                                    case 5:
                                        damageModifier = 2.6;
                                        break;
                                    default:
                                        damageModifier = 2.75;
                                        break;
                                }
                                break;
                            }
                        }

                        int actualDamage = (int)(damage * damageModifier);
                        e.setDamage(actualDamage);

                        if (GlobalQuestPlugin.isDebugEnabled) {
                            System.out.print("Base damage: " + damage + ", damage modifier: " + damageModifier + ", actual damage: " + actualDamage);
                        }

                        return;
                    }

                    // 2. If Isaak Breen is the damagee, he should receive half or third part of the damage:
                    if (damagee.getName().equals("")) {
                        double defenceModifier = 1.5;
                        switch (playersOnline) {
                            case 1:
                                defenceModifier *= 1.0;
                                break;
                            case 2:
                                defenceModifier = 1.75;
                                break;
                            case 3:
                                defenceModifier = 1.85;
                                break;
                            case 4:
                                defenceModifier = 1.9;
                                break;
                            default: // 5 and more:
                                defenceModifier = 2.0;
                                break;
                        }
                        int actualDamage = (int)(damage / defenceModifier);
                        e.setDamage(actualDamage);

                        if (GlobalQuestPlugin.isDebugEnabled) {
                            System.out.print("Base damage: " + damage + ", defence modifier: " + defenceModifier + ", actual damage: " + actualDamage);
                        }

                        return;
                    }
                }
            }

            // Disable damage for jetpack and set half damage for general fall:
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
                if (e.getEntity() instanceof Player) {
                    Player damagee = (Player)e.getEntity();
                    PlayerInventory inventory = damagee.getInventory();
                    ItemStack armorSlot = inventory.getChestplate();
                                       
                    if (armorSlot.getType().getId() == GlobalQuestPlugin.jetpackId) {
                        event.setDamage(0);
                    }
                    else {
                         // Original damage value:
                        int previousDamage = e.getDamage();

                         // If Isaak Breen is the damagee, he should receive half of the damage:
                        if (damagee.getName().equals("")) {
                            e.setDamage(previousDamage / 2);
                        }
                        else {
                            e.setDamage(previousDamage);
                        }
                    }
                }
            }

            // The same — for fire damage:
            if ((event.getCause().equals(EntityDamageEvent.DamageCause.FIRE))
                || (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK))) {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
                if (e.getEntity() instanceof Player) {
                    Player damagee = (Player)e.getEntity();

                    // Original damage value:
                    int previousDamage = e.getDamage();

                     // If Isaak Breen is the damagee, he should receive half of the damage:
                    if (damagee.getName().equals("")) {
                        e.setDamage(previousDamage / 2);
                    }
                    else {
                        e.setDamage(previousDamage);
                    }
                }
            }
        }
    }

    /**
     * Handles EXPLOSION_PRIME event. Disables dynamite explosions on airbases.
     * @param event ExplosionPrimeEvent.
     */
    @Override
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            TNTPrimed tnt = (TNTPrimed)event.getEntity();
            AirBase airBase = GlobalQuestPlugin.airBases.get(tnt.getWorld());
            Location explosionLocation = tnt.getLocation();
            
            // 5.0 is a MAGIC number.
            if (explosionLocation.distance(airBase.airbaseCenterCoordinates) >= airBase.domeRadius + 5.0) {
                if (GlobalQuestPlugin.isDebugEnabled) {
                    System.out.print("Explosion happened far away from the airbase.");
                }
            }
            else {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Handles Isaak's death and removes any drops from him.
     * @param event Entity death event.
     */
    @Override
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player)entity;

            // Isaak Breen
            if (player.getName().equals("")) {
                List<ItemStack> drops = event.getDrops();
                drops.clear();
            }
        }
    }
}
