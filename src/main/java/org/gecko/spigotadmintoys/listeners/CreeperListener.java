package org.gecko.spigotadmintoys.listeners;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.gecko.spigotadmintoys.Main;
import org.gecko.spigotadmintoys.data.ConfigurationManager;
import org.gecko.spigotadmintoys.logic.SetAndGet;

public class CreeperListener implements Listener {
    private final SetAndGet setAndGet;
    private Location creeperLocation;
    private final ConfigurationManager configurationManager;

    public CreeperListener(ConfigurationManager configManager, Main plugin) {
        this.configurationManager = configManager;
        this.setAndGet = plugin.getSetAndGet();
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
                setAndGet.getBedrockListener().bedrockValueAssignHandler(null, "creeper");
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