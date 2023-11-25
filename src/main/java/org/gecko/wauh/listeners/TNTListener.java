package org.gecko.wauh.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class TNTListener implements Listener {

    public Location tntLocation;
    public Player tntPlayer = null;

    @EventHandler
    public void onTNTExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();

        // Check if the exploding entity is TNT
        if (entity instanceof TNTPrimed) {
            event.setCancelled(true); // Cancel the normal explosion

            // Get the location of the TNT explosion
            TNTPrimed tnt = (TNTPrimed) entity;
            if (tnt.getLocation() != null) {
                if (tnt.getSource() instanceof Player) {
                    tntPlayer = (Player) tnt.getSource();
                } else {
                    tntPlayer = null;
                }

                tntLocation = tnt.getLocation();
                BedrockListener bedrockListener = new BedrockListener();
                bedrockListener.BedrockClick(null);
            }
        }
    }
}
