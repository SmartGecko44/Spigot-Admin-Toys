package org.gecko.wauh.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.wauh.Main;
import org.gecko.wauh.data.ConfigurationManager;

public class TNTListener implements Listener {

    private final ConfigurationManager configManager;
    private Location tntLocation;
    private Player tntPlayer;

    private TNTPrimed tnt;

    public TNTListener(ConfigurationManager configurationManager) {
        this.configManager = configurationManager;
    }

    @EventHandler
    //FIXME: TNT often throws a lot of errors
    public void onTNTExplode(EntityExplodeEvent event) {
        FileConfiguration config;
        config = configManager.getConfig();
        if (config.getInt("TNT enabled") == 0) {
            return;
        }
        Entity entity = event.getEntity();

        // Check if the exploding entity is TNT
        if (entity instanceof TNTPrimed tntreal) {
            event.setCancelled(true); // Cancel the normal explosion
            setTnt(tntreal);

            // Get the location of the TNT explosion
            if (tntreal.getLocation() != null) {
                if (tntreal.getSource() instanceof Player player) {
                    setTntPlayer(player);
                } else if (tntreal.getMetadata("Source").getFirst().asString() != null) {
                    Bukkit.getPlayer(tntreal.getMetadata("Source").getFirst().asString());
                } else {
                    return;
                }
                setTntLocation(tntreal.getLocation());
                JavaPlugin.getPlugin(Main.class).getSetAndGet().getBedrockListener().bedrockValueAssignHandler(null, "TNT");
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
        return this.tntPlayer;
    }

    public void setTntPlayer(Player tntPlayer) {
        this.tntPlayer = tntPlayer;
    }

    public TNTPrimed getTnt() {
        return tnt;
    }

    public void setTnt(TNTPrimed tnt) {
        this.tnt = tnt;
    }
}
