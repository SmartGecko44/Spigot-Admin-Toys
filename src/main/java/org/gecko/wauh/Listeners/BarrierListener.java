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

public class BarrierListener implements Listener {

    public Player currentRemovingPlayer;
    public boolean stopBlockRemoval = false;
    public boolean blockRemovalActive = false;
    private int grassRemovedCount = 0;
    private int dirtRemovedCount = 0;
    private int barrierRemovedCount = 0;
    private Set<Block> blocksToProcess = new HashSet<>();
    private Location clickedLocation;
    private boolean limitReached = false;
    private int highestDist = 0;

    @EventHandler
    public void BarrierClick(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().getType() == Material.GRASS || event.getBlock().getType() == Material.DIRT || event.getBlock().getType() == Material.BARRIER) {
            // Check if the bucket is filling with water
            if (player.getInventory().getItemInMainHand().getType() == Material.BARRIER) {
                blockRemovalActive = true;
                limitReached = false;
                clickedLocation = event.getBlock().getLocation();

                // Reset the water removal counts and initialize the set of blocks to process
                grassRemovedCount = 0;
                dirtRemovedCount = 0;
                barrierRemovedCount = 0;
                highestDist = 0;
                blocksToProcess.clear();
                currentRemovingPlayer = player;

                // Add the clicked block to the set of blocks to process
                blocksToProcess.add(clickedLocation.getBlock());

                // Start the water removal process
                processBlockRemoval();
            }
        }
    }

    private void processBlockRemoval() {
        int radiusLimit = Main.getPlugin(Main.class).getRadiusLimit();
        int realRadiusLimit = radiusLimit - 2;
        if (stopBlockRemoval) {
            stopBlockRemoval = false;
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
                        currentRemovingPlayer.sendMessage(ChatColor.GREEN + "Block removal: " + ChatColor.RED + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit);
                    } else if (highestDist == realRadiusLimit) {
                        currentRemovingPlayer.sendMessage(ChatColor.GREEN + "Block removal: " + ChatColor.GREEN + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit);
                    }
                } else {
                    limitReached = true;
                    limitReachedThisIteration = true;
                }
            }

            // Check if the block is grass or dirt
            if (block.getType() == Material.GRASS) {
                grassRemovedCount++;
            } else if (block.getType() == Material.DIRT) {
                dirtRemovedCount++;
            } else if (block.getType() == Material.BARRIER) {
                barrierRemovedCount++;
            }

            block.setType(Material.AIR);

            // Iterate through neighboring blocks and add them to the next set
            for (int i = -1; i <= 1; i++) {
                if (i == 0) continue; // Skip the current block
                Block neighboringBlockX = block.getRelative(i, 0, 0);
                Block neighboringBlockY = block.getRelative(0, i, 0);
                Block neighboringBlockZ = block.getRelative(0, 0, i);

                if ((neighboringBlockX.getType() == Material.GRASS || neighboringBlockX.getType() == Material.DIRT || neighboringBlockX.getType() == Material.BARRIER)) {
                    nextSet.add(neighboringBlockX);
                }
                if ((neighboringBlockY.getType() == Material.GRASS || neighboringBlockY.getType() == Material.DIRT || neighboringBlockY.getType() == Material.BARRIER)) {
                    nextSet.add(neighboringBlockY);
                }
                if ((neighboringBlockZ.getType() == Material.GRASS || neighboringBlockZ.getType() == Material.DIRT || neighboringBlockZ.getType() == Material.BARRIER)) {
                    nextSet.add(neighboringBlockZ);
                }
            }
        }

        blocksToProcess = nextSet;

        if (limitReachedThisIteration) {
            barriuhFin();
        } else if (!blocksToProcess.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::processBlockRemoval, 2L);
        } else {
            displaySummary();
        }
    }

    private void barriuhFin() {
        // Check if there are more blocks to process
        if (limitReached) {
            displaySummary();
        } else if (!blocksToProcess.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::processBlockRemoval, 2L);
        } else {
            displaySummary();
        }
    }

    public void displaySummary() {
        Player player = currentRemovingPlayer;
        // Display the block removal summary to the player
        if (grassRemovedCount + dirtRemovedCount + barrierRemovedCount > 1) {
            if (barrierRemovedCount == 0) {
                player.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + grassRemovedCount + ChatColor.GREEN + " grass blocks and " + ChatColor.RED + dirtRemovedCount + ChatColor.GREEN + " dirt blocks.");
            } else if (barrierRemovedCount > 0) {
                player.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + grassRemovedCount + ChatColor.GREEN + " grass blocks, " + ChatColor.RED + dirtRemovedCount + ChatColor.GREEN + " dirt blocks and " + ChatColor.RED + barrierRemovedCount + ChatColor.GREEN + " barrier blocks.");
            }
            // Display the block removal summary in the console
            Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GREEN + " removed " + ChatColor.RED + grassRemovedCount + ChatColor.GREEN + " grass blocks, " + ChatColor.RED + dirtRemovedCount + ChatColor.GREEN + " dirt blocks and " + ChatColor.RED + barrierRemovedCount + ChatColor.GREEN + " barriers");
            blockRemovalActive = false;
            currentRemovingPlayer = null;
        }
    }
}