package org.gecko.wauh.blocks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

public class Mirror implements Listener {
    private final Plugin plugin;

    public Mirror(Plugin plugin) {
        this.plugin = plugin;
    }

    public void MirrorLogic(Player player) {
        // Additional logic if needed
    }

    public Block getTargetBlock(Player player, int distance) {
        BlockIterator blockIterator = new BlockIterator(player, distance);

        while (blockIterator.hasNext()) {
            Block block = blockIterator.next();
            if (!block.isEmpty()) {
                return block;
            }
        }

        return null;
    }

    public void mainRayCast(Player player) {
        int maxDistance = 100; // You can adjust this distance

        Block targetBlock = getTargetBlock(player, maxDistance);
        if (targetBlock != null) {
            // Do something with the target block
            player.sendMessage("Hit block: " + targetBlock.getType());
        }
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
