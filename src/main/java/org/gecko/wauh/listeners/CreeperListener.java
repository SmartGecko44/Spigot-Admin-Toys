package org.gecko.wauh.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class CreeperListener implements Listener {

    public Location creeperLocation;
    public Player creeperPlayer = null;

    @EventHandler
    public void onCreeperExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();

        // Check if the exploding entity is TNT
        if (entity instanceof Creeper) {
            event.setCancelled(true); // Cancel the normal explosion

            // Get the location of the TNT explosion
            Creeper creeper = (Creeper) entity;
            if (creeper.getLocation() != null) {
                {
                    creeperLocation = creeper.getLocation();
                    BedrockListener bedrockListener = new BedrockListener();
                    bedrockListener.BedrockClick(null);
                }
            }
        }
    }
}