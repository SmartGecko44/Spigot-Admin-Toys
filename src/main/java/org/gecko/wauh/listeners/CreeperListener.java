package org.gecko.wauh.listeners;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.gecko.wauh.Main;
import org.gecko.wauh.data.ConfigurationManager;

public class CreeperListener implements Listener {

    public Location creeperLocation;

    @EventHandler
    public void onCreeperExplode(EntityExplodeEvent event) {
        ConfigurationManager configManager;
        FileConfiguration config;
        configManager = new ConfigurationManager(Main.getPlugin(Main.class));
        config = configManager.getConfig();
        if (config.getInt("Creeper enabled") == 0) {
            return;
        }
        Entity entity = event.getEntity();
        // Check if the exploding entity is TNT
        if (entity instanceof Creeper creeper) {

            event.setCancelled(true); // Cancel the normal explosion

            // Get the location of the TNT explosion
            if (creeper.getLocation() != null) {
                creeperLocation = creeper.getLocation();
                BedrockListener bedrockListener = new BedrockListener();
                bedrockListener.bedrockValueAssignHandler(null, "creeper");
            }
        }
    }
}