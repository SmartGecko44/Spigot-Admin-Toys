package org.gecko.wauh.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.gecko.wauh.Main;

import java.util.HashSet;
import java.util.Set;

public class BedrockListener implements Listener {

    public Player currentRemovingPlayer;
    public boolean stopAllRemoval = false;
    public boolean allRemovalActive = false;
    private int allRemovedCount;
    private Set<Block> blocksToProcess = new HashSet<>();
    private Location clickedLocation;
    private boolean limitReached = false;
    private int highestDist = 0;

    @EventHandler
    public void BedrockClick(BlockBreakEvent event) {
        BucketListener bucketListener = Main.getPlugin(Main.class).getBucketListener();
        BarrierListener barrierListener = Main.getPlugin(Main.class).getBarrierListener();
        WaterBucketListener waterBucketListener = Main.getPlugin(Main.class).getWaterBucketListener();
        if (!bucketListener.wauhRemovalActive && !barrierListener.blockRemovalActive && !waterBucketListener.tsunamiActive) {
            Player player = event.getPlayer();
            // Check if the bucket is filling with water
            if (player.getInventory().getItemInMainHand().getType() == Material.BEDROCK) {
                if (event.getBlock().getType() != Material.BEDROCK) {
                    allRemovalActive = true;
                    limitReached = false;
                    clickedLocation = event.getBlock().getLocation();

                    // Reset the water removal counts and initialize the set of blocks to process
                    highestDist = 0;
                    allRemovedCount = 0;
                    blocksToProcess.clear();
                    currentRemovingPlayer = player;

                    // Add the clicked block to the set of blocks to process
                    blocksToProcess.add(clickedLocation.getBlock());

                    // Start the water removal process
                    processAllRemoval();
                }
            }
        }
    }

    private void processAllRemoval() {
        int radiusLimit = Main.getPlugin(Main.class).getRadiusLimit();
        int realRadiusLimit = radiusLimit - 2;
        if (stopAllRemoval) {
            stopAllRemoval = false;
            displaySummary();
            return;
        }
        Set<Block> nextSet = new HashSet<>();
        boolean limitReachedThisIteration = false; // Variable to track whether the limit was reached this iteration
        for (Block block : blocksToProcess) {
            int dist = (int) clickedLocation.distance(block.getLocation());
            if (dist > radiusLimit) {
                limitReached = true;
                limitReachedThisIteration = true;
            }
            if (dist > highestDist) {
                if (highestDist <= (realRadiusLimit - 1)) {
                    highestDist = dist;
                    // Send a message to the player only when the dist value rises
                    if (highestDist < realRadiusLimit) {
                        currentRemovingPlayer.sendMessage(ChatColor.GREEN + "All removal: " + ChatColor.RED + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit);
                    } else if (highestDist == realRadiusLimit) {
                        currentRemovingPlayer.sendMessage(ChatColor.GREEN + "All removal: " + ChatColor.GREEN + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit);
                    }
                } else {
                    limitReached = true;
                    limitReachedThisIteration = true;
                }
            }

            // Check if the block is grass or dirt
            allRemovedCount++;

            block.setType(Material.AIR);

            // Iterate through neighboring blocks and add them to the next set
            for (int i = -1; i <= 1; i++) {
                if (i == 0) continue; // Skip the current block
                Block neighboringBlockX = block.getRelative(i, 0, 0);
                Block neighboringBlockY = block.getRelative(0, i, 0);
                Block neighboringBlockZ = block.getRelative(0, 0, i);

                if (neighboringBlockX.getType() != Material.AIR
                        && neighboringBlockX.getType() != Material.BEDROCK
                        && neighboringBlockX.getType() != Material.STATIONARY_WATER
                        && neighboringBlockX.getType() != Material.WATER
                        && neighboringBlockX.getType() != Material.LAVA
                        && neighboringBlockX.getType() != Material.STATIONARY_LAVA) {
                    nextSet.add(neighboringBlockX);
                }
                if (neighboringBlockY.getType() != Material.AIR
                        && neighboringBlockY.getType() != Material.BEDROCK
                        && neighboringBlockY.getType() != Material.STATIONARY_WATER
                        && neighboringBlockY.getType() != Material.WATER
                        && neighboringBlockY.getType() != Material.LAVA
                        && neighboringBlockY.getType() != Material.STATIONARY_LAVA) {
                    nextSet.add(neighboringBlockY);
                }
                if (neighboringBlockZ.getType() != Material.AIR
                        && neighboringBlockZ.getType() != Material.BEDROCK
                        && neighboringBlockZ.getType() != Material.STATIONARY_WATER
                        && neighboringBlockZ.getType() != Material.WATER
                        && neighboringBlockZ.getType() != Material.LAVA
                        && neighboringBlockZ.getType() != Material.STATIONARY_LAVA) {
                    nextSet.add(neighboringBlockZ);
                }
            }

        }

        blocksToProcess = nextSet;

        if (limitReachedThisIteration) {
            bedrockFin();
        } else if (!blocksToProcess.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::processAllRemoval, 2L);
        } else {
            displaySummary();
        }
    }

    private void bedrockFin() {
        // Check if there are more blocks to process
        if (limitReached) {
            displaySummary();
        } else if (!blocksToProcess.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::processAllRemoval, 2L);
        } else {
            displaySummary();
        }
    }

    public void displaySummary() {
        Player player = currentRemovingPlayer;
        // Display the block removal summary to the player
        if (allRemovedCount > 1) {
            player.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + allRemovedCount + ChatColor.GREEN + " blocks.");
            // Display the block removal summary in the console
            Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GREEN + " removed " + ChatColor.RED + allRemovedCount + ChatColor.GREEN + " blocks.");
        }
        allRemovalActive = false;
        currentRemovingPlayer = null;
    }
}