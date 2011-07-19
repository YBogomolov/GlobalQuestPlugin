/**
 * User: YBogomolov
 * Date: 14.07.11
 * Time: 13:17
 */

package com.github.doodlez.bukkit.globalquest;

import com.github.doodlez.bukkit.globalquest.utilities.AirBase;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.ExplosionPrimeEvent;

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

                    int damage = e.getDamage();
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
                        int actualDamage = GlobalQuestPlugin.playerBaseDamage + playersOnline * GlobalQuestPlugin.playerDamageModifier;
                        actualDamage *= 2;

                        e.setDamage(actualDamage);
                        return;
                    }

                    // 2. If Isaak Breen is the damagee, he should receive half or third part of the damage:
                    if (damagee.getName().equals("")) {
                        int damageModifier = 2;
                        if (playersOnline >= 2)
                            damageModifier = 3;
                        if (playersOnline >= 4)
                            damageModifier = 4;
                        damage /= damageModifier;
                        e.setDamage(damage);
                        return;
                    }
                }
            }

            // The same — for fall and fire damage:
            if ((event.getCause().equals(EntityDamageEvent.DamageCause.FALL))
                || (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE))
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
}
