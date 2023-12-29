package org.gecko.wauh.blocks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class Mirror implements Listener {
    private final Plugin plugin;

    public Mirror(Plugin plugin) {
        this.plugin = plugin;
    }

    public void MirrorLogic(Player player) {
        // Additional logic if needed
    }
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        scheduleRayCastTask(player);
    }

    public void mainRayCast(Player player) {
        int maxDistance = 100;

        Block targetBlock = getTargetBlock(player, maxDistance);

        if (targetBlock != null) {
            player.sendMessage("Hit block: " + targetBlock.getType());

            // Check if the hit block is glass
            if (targetBlock.getType() == Material.GLASS) {
                // Calculate the reflection vector
                Vector direction = player.getLocation().getDirection();
                Vector normal = getGlassBlockNormal();

                Vector reflection = direction.subtract(normal.multiply(direction.dot(normal) * 2));

                // Continue the ray from the glass surface
                Vector glassSurfacePoint = new Vector(targetBlock.getX() + 0.5, targetBlock.getY() + 0.5, targetBlock.getZ() + 0.5);
                Vector reflectedPoint = glassSurfacePoint.clone().add(reflection.multiply(0.1)); // Multiply by a small factor to avoid infinite loops

                Block reflectedBlock = player.getWorld().getBlockAt(reflectedPoint.toLocation(player.getWorld()));

                player.sendMessage("Reflected block: " + reflectedBlock.getType());
            }
        }
    }

    private Vector getGlassBlockNormal() {
        // Calculate the normal vector of the glass block (for simplicity, assuming it's a flat surface)
        return new Vector(0, 1, 0); // Change this based on the orientation of your glass block
    }

    private Block getTargetBlock(Player player, int distance) {
        BlockIterator blockIterator = new BlockIterator(player, distance);

        while (blockIterator.hasNext()) {
            Block block = blockIterator.next();
            if (!block.isEmpty()) {
                return block;
            }
        }

        return null;
    }

    public void scheduleRayCastTask(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                mainRayCast(player);
            }
        }.runTaskTimer(plugin, 0L, 2L); // 0L initial delay, 2L ticks between each run
    }
}
