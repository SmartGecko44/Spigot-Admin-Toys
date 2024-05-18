package org.gecko.spigotadmintoys.listeners;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.spigotadmintoys.Main;
import org.gecko.spigotadmintoys.data.ConfigurationManager;

public class CreeperListener implements Listener {
    private final ConfigurationManager configurationManager;
    private Location creeperLocation;

    public CreeperListener(ConfigurationManager configManager) {
        this.configurationManager = configManager;
    }

    @EventHandler
    public void onCreeperExplode(EntityExplodeEvent event) {
        FileConfiguration config;
        config = configurationManager.getConfig();
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
                JavaPlugin.getPlugin(Main.class).getSetAndGet().getBedrockListener().bedrockValueAssignHandler(null, "creeper");
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