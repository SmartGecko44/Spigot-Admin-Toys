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

    private Location creeperLocation;
    private static final Main plugin = new Main();

    @EventHandler
    public void onCreeperExplode(EntityExplodeEvent event) {
        ConfigurationManager configManager;
        FileConfiguration config;
        configManager = new ConfigurationManager(plugin);
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
                setCreeperLocation(creeper.getLocation());
                BedrockListener bedrockListener = new BedrockListener();
                bedrockListener.bedrockValueAssignHandler(null, "creeper");
            }
        }
    }

    public Location getCreeperLocation() {
        return creeperLocation;
    }

    public void setCreeperLocation(Location creeperLocation) {
        this.creeperLocation = creeperLocation;
    }
}