/**
 * User: YBogomolov
 * Date: 14.07.11
 * Time: 13:17
 */

package com.github.doodlez.bukkit.globalquest;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class SpecialEntityListener extends EntityListener {
    /**
     * Handles ENTITY_DAMAGE event. Sets appropriate damage to Herobrine.
     * @param event EntityDamageEvent.
     */
    @Override
    public void onEntityDamage(EntityDamageEvent event) {
        // Only process when both damager and damagee are Entities.
        if (event instanceof EntityDamageByEntityEvent) {
            // Let's set appropriae damage values for Herobrine if he attacks player
            // or he is attacked by player.
            if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
                //Check to see if the damager and damaged are players
                if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
                    Player damager = (Player)e.getDamager();
                    Player damagee = (Player)e.getEntity();

                    // Original damage value:
                    int previousDamage = e.getDamage();

                    // Now we have options:
                    // 1. If Herobrine is damager, he sould deal DOUBLE damage:
                    if (damager.getName().equals("")) {
                        e.setDamage(previousDamage * 2);
                        return;
                    }

                    // 2. If Herobrine is the damagee, hesould receive HALF of damage:
                    if (damagee.getName().equals("")) {
                        e.setDamage(previousDamage / 2);
                        return;
                    }
                }
            }

            // The same — for fall damage:
            if ((event.getCause().equals(EntityDamageEvent.DamageCause.FALL))
                || (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE))
                || (event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK))) {
                EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
                if (e.getEntity() instanceof Player) {
                    Player damagee = (Player)e.getEntity();

                    // Original damage value:
                    int previousDamage = e.getDamage();

                     // If Herobrine is the damagee, hesould receive HALF of damage:
                    if (damagee.getName().equals("")) {
                        e.setDamage(previousDamage / 2);
                    }
                }
            }
        }
    }
}
