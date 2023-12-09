package org.gecko.wauh.listeners;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.gecko.wauh.Main;
import org.gecko.wauh.data.ConfigurationManager;

public class TNTListener implements Listener {

    public Location tntLocation;
    public Player tntPlayer = null;

    @EventHandler
    public void onTNTExplode(EntityExplodeEvent event) {
        ConfigurationManager configManager;
        FileConfiguration config;
        configManager = new ConfigurationManager(Main.getPlugin(Main.class));
        config = configManager.getConfig();
        if (config.getInt("TNT enabled") == 0) {
            return;
        }
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
                bedrockListener.bedrockValueAssignHandler(null, "TNT");
            }
        }
    }
}
