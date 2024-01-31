package org.gecko.wauh.blocks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class Mirror implements Listener {
    private final Plugin plugin;
    private Block lastGlassBlock = null;

    public Mirror(Plugin plugin) {
        this.plugin = plugin;
    }

    public void mirrorLogic(Player player) {
        // Additional logic if needed
    }
    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        scheduleRayCastTask(player);
    }

    public void mainRayCast(Player player) {
        int maxDistance = 100;

        Block targetBlock = getTargetBlock(player, maxDistance);

        if (targetBlock != null) {
            if (targetBlock.getType() == Material.GLASS) {
                // Simulate reflection on glass
                if (lastGlassBlock != null) {
                    Vector direction = player.getEyeLocation().getDirection();

                    // Calculate the reflection vector
                    Vector reflection = direction.subtract(lastGlassBlock.getLocation().toVector()).multiply(-2).multiply(direction.dot(lastGlassBlock.getLocation().toVector()));

                    // Get the block face the player is looking at
                    BlockFace blockFace = getBlockFace(player);

                    // Move the starting point slightly away from the glass to avoid self-intersection
                    Location reflectionStart = lastGlassBlock.getRelative(blockFace).getLocation();

                    // Cast the reflected ray
                    Block reflectedBlock = getTargetBlockFromLocation(reflectionStart, maxDistance);

                    // Handle the reflected block as needed
                    if (reflectedBlock != null) {
                        player.sendMessage("Reflected block: " + reflectedBlock.getType());
                        // Visualize the reflection path with a particle trail
                        spawnParticleTrail(player, reflectionStart, reflection, reflectedBlock.getLocation());
                        reflectedBlock.setType(Material.GLOWSTONE);
                    } else {
                        // Visualize the reflection path with a particle trail
                        spawnParticleTrail(player, reflectionStart, reflection, null);
                    }
                }
            } else {
                // Do something with the regular target block
                player.sendMessage("Hit block: " + targetBlock.getType());
            }
        }
    }

    private BlockFace getBlockFace(Player player) {
        float yaw = player.getLocation().getYaw();
        if (yaw < 0) {
            yaw += 360;
        }

        if (yaw >= 45 && yaw < 135) {
            return BlockFace.WEST;
        } else if (yaw >= 135 && yaw < 225) {
            return BlockFace.NORTH;
        } else if (yaw >= 225 && yaw < 315) {
            return BlockFace.EAST;
        } else {
            return BlockFace.SOUTH;
        }
    }

    private void spawnParticleTrail(Player player, Location start, Vector direction, Location end) {
        int particleCount = 100;
        double interval = 0.1;

        direction.normalize().multiply(interval);

        Particle startParticle = Particle.TOTEM; // Change to your desired starting particle
        Particle endParticle = Particle.BARRIER; // Change to your desired ending particle

        for (int i = 0; i < particleCount; i++) {
            Location particleLocation = start.clone().add(direction.clone().multiply(i));

            // Use different particles for the beginning and end of the trail
            Particle particleType;
            if (end != null && particleLocation.distanceSquared(end) < 1) {
                particleType = endParticle;
            } else {
                particleType = (i == 0) ? startParticle : Particle.REDSTONE; // Change to your desired particle for the trail
            }

            // Adjust particle effects as needed
            player.spawnParticle(particleType, particleLocation, 1);
        }
    }


    private Block getTargetBlockFromLocation(Location location, int distance) {
        BlockIterator blockIterator = new BlockIterator(location.getWorld(), location.toVector(), location.getDirection(), 0, distance);

        while (blockIterator.hasNext()) {
            Block block = blockIterator.next();
            if (!block.isEmpty()) {
                return block;
            }
        }

        return null;
    }


    public Block getTargetBlock(Player player, int distance) {
        BlockIterator blockIterator = new BlockIterator(player, distance);

        while (blockIterator.hasNext()) {
            Block block = blockIterator.next();
            if (!block.isEmpty()) {
                if (block.getType() == Material.GLASS) {
                    lastGlassBlock = block; // Update the last glass block
                }
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
