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
import org.gecko.wauh.logic.SetAndGet;

public class TNTListener implements Listener {

    private final Main plugin;
    private final SetAndGet setAndGet;
    private Location tntLocation;
    private Player tntPlayer = null;

    public TNTListener(Main plugin, SetAndGet setAndGet) {
        this.plugin = plugin;
        this.setAndGet = setAndGet;
    }

    @EventHandler
    public void onTNTExplode(EntityExplodeEvent event) {
        ConfigurationManager configManager;
        FileConfiguration config;
        configManager = new ConfigurationManager(plugin);
        config = configManager.getConfig();
        if (config.getInt("TNT enabled") == 0) {
            return;
        }
        Entity entity = event.getEntity();

        // Check if the exploding entity is TNT
        if (entity instanceof TNTPrimed tnt) {
            event.setCancelled(true); // Cancel the normal explosion

            // Get the location of the TNT explosion
            if (tnt.getLocation() != null) {
                if (tnt.getSource() instanceof Player player) {
                    setTntPlayer(player);
                    if (!getTntPlayer().isOp()) {
                        return;
                    }
                } else {
                    setTntPlayer(null);
                }

                setTntLocation(tnt.getLocation());
                BedrockListener bedrockListener = new BedrockListener(setAndGet);
                bedrockListener.bedrockValueAssignHandler(null, "TNT");
            }
        }
    }

    public Location getTntLocation() {
        return tntLocation;
    }

    public void setTntLocation(Location tntLocation) {
        this.tntLocation = tntLocation;
    }

    public Player getTntPlayer() {
        return tntPlayer;
    }

    public void setTntPlayer(Player tntPlayer) {
        this.tntPlayer = tntPlayer;
    }
}
