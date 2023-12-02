package org.gecko.wauh.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.gecko.wauh.Main;
import org.gecko.wauh.logic.ScaleReverse;

import java.util.*;

public class WaterBucketListener implements Listener {

    private final Set<Block> markedBlocks = new HashSet<>();
    private final Set<Block> processedBlocks = new HashSet<>();
    private final Set<Block> removedBlocks = new HashSet<>();
    public Player currentRemovingPlayer;
    public boolean stopTsunami = false;
    public boolean tsunamiActive = false;
    private int waterPlacedCount;
    private Set<Block> blocksToProcess = new HashSet<>();
    private Location clickedLocation;
    private boolean limitReached = false;
    private int highestDist = 0;
    private int dist;
    private int radiusLimit;
    private int realRadiusLimit;

    @EventHandler
    public void TsunamiClick(PlayerBucketEmptyEvent event) {
        BucketListener bucketListener = Main.getPlugin(Main.class).getBucketListener();
        BarrierListener barrierListener = Main.getPlugin(Main.class).getBarrierListener();
        BedrockListener bedrockListener = Main.getPlugin(Main.class).getBedrockListener();
        radiusLimit = Main.getPlugin(Main.class).getRadiusLimit();
        realRadiusLimit = radiusLimit - 2;
        if (realRadiusLimit > 1) {
            if (!bucketListener.wauhRemovalActive && !barrierListener.blockRemovalActive && !bedrockListener.allRemovalActive && !tsunamiActive) {
                Player player = event.getPlayer();
                // Check if the bucket is filling with water
                if (player.getInventory().getItemInMainHand().getType() == Material.WATER_BUCKET) {
                    if (player.isSneaking()) {
                        tsunamiActive = true;
                        limitReached = false;
                        clickedLocation = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();

                        // Reset the water removal counts and initialize the set of blocks to process
                        highestDist = 0;
                        waterPlacedCount = 0;
                        blocksToProcess.clear();
                        currentRemovingPlayer = player;

                        // Add the clicked block to the set of blocks to process
                        blocksToProcess.add(clickedLocation.getBlock());

                        // Start the water removal process
                        processTsunami();
                    }
                }
            }
        }
    }

    private void processTsunami() {
        if (stopTsunami) {
            stopTsunami = false;
            displaySummary();
            return;
        }
        Set<Block> nextSet = new HashSet<>();
        boolean limitReachedThisIteration = false; // Variable to track whether the limit was reached this iteration
        for (Block block : blocksToProcess) {
            if (processedBlocks.contains(block)) {
                continue;
            }
            dist = (int) clickedLocation.distance(block.getLocation()) + 1;
            if (dist > radiusLimit - 3) {
                limitReached = true;
                limitReachedThisIteration = true;
            }
            if ((dist - 1) > highestDist) {
                if (dist > 1) {
                    int progressPercentage = (int) ((double) highestDist / (realRadiusLimit - 2) * 100);
                    highestDist = dist - 1;
                    // Send a message to the player only when the dist value rises
                    if (highestDist < realRadiusLimit - 1) {
                        currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Tsunami: " + ChatColor.RED + progressPercentage + "% " + ChatColor.GREEN + "(" + ChatColor.RED + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                    } else if (!limitReachedThisIteration) {
                        currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Tsunami: " + ChatColor.GREEN + progressPercentage + "% (" + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                    } else {
                        currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Tsunami: " + ChatColor.GREEN + "100% " + "(" + realRadiusLimit + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                    }
                }
            }

            waterPlacedCount++;
            if (block.getType() == Material.AIR) {
                if (Main.getPlugin(Main.class).getShowRemoval()) {
                    block.setType(Material.WATER);
                } else {
                    markedBlocks.add(block);
                }
            }

            // Iterate through neighboring blocks and add them to the next set
            for (int i = -1; i <= 1; i++) {
                if (i == 0) continue; // Skip the current block
                Block neighboringBlockX = block.getRelative(i, 0, 0);
                Block neighboringBlockY = block.getRelative(0, -1, 0);
                Block neighboringBlockZ = block.getRelative(0, 0, i);

                if ((neighboringBlockX.getType() == Material.AIR)) {
                    nextSet.add(neighboringBlockX);
                }
                if ((neighboringBlockY.getType() == Material.AIR)) {
                    nextSet.add(neighboringBlockY);
                }
                if ((neighboringBlockZ.getType() == Material.AIR)) {
                    nextSet.add(neighboringBlockZ);
                }
            }
            processedBlocks.add(block);
        }

        blocksToProcess = nextSet;

        if (limitReachedThisIteration) {
            TsunamiFin();
        } else if (!blocksToProcess.isEmpty()) {
            if (Main.getPlugin(Main.class).getShowRemoval()) {
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::processTsunami, 2L);
            } else {
                processTsunami();
            }
        } else {
            if (dist > 0) {
                currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Tsunami: " + ChatColor.GREEN + "100% " + "(" + realRadiusLimit + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
            }
            displaySummary();
        }
    }

    private void TsunamiFin() {
        // Check if there are more blocks to process
        if (limitReached) {
            displaySummary();
        } else if (!blocksToProcess.isEmpty()) {
            if (Main.getPlugin(Main.class).getShowRemoval()) {
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::processTsunami, 2L);
            } else {
                processTsunami();
            }
        } else {
            displaySummary();
        }
    }

    public void displaySummary() {
        Player player = currentRemovingPlayer;
        // Display the block removal summary to the player
        if (waterPlacedCount > 1) {
            player.sendMessage(ChatColor.GREEN + "Placed " + ChatColor.RED + waterPlacedCount + ChatColor.GREEN + " water blocks.");
            // Display the block removal summary in the console
            Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GREEN + " placed " + ChatColor.RED + waterPlacedCount + ChatColor.GREEN + " water blocks.");
            if (!Main.getPlugin(Main.class).getShowRemoval()) {
                removeMarkedBlocks();
            } else {
                tsunamiActive = false;
                currentRemovingPlayer = null;
                stopTsunami = false;
                blocksToProcess.clear();
                markedBlocks.clear();
                processedBlocks.clear();
                removedBlocks.clear();
            }
        } else {
            tsunamiActive = false;
            currentRemovingPlayer = null;
            stopTsunami = false;
            blocksToProcess.clear();
            markedBlocks.clear();
            processedBlocks.clear();
            removedBlocks.clear();
        }
    }

    private void removeMarkedBlocks() {
        ScaleReverse scaleReverse = Main.getPlugin(Main.class).getScaleReverse();

        int totalRemovedCount = waterPlacedCount;
        if (totalRemovedCount < 50000) {
            for (Block block : markedBlocks) {
                block.setType(Material.STATIONARY_WATER);
            }
            tsunamiActive = false;
            currentRemovingPlayer = null;
            stopTsunami = false;
            blocksToProcess.clear();
            markedBlocks.clear();
            processedBlocks.clear();
            removedBlocks.clear();
        } else {
            scaleReverse.ScaleReverseLogic(totalRemovedCount, radiusLimit, markedBlocks, "wauh");
        }

        // If there are more blocks to remove, schedule the next batch
        if (!markedBlocks.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::removeMarkedBlocks, 1L); // Schedule the next batch after 1 tick
        } else if (!removedBlocks.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::removeMarkedBlocks, 100L);
            // If all blocks have been processed, but there are blocks in the removedBlocks set,
            // process those in the next iteration.
            currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Water placement finished"));
            tsunamiActive = false;
            currentRemovingPlayer = null;
            stopTsunami = false;
            blocksToProcess.clear();
            markedBlocks.clear();
            processedBlocks.clear();
            removedBlocks.clear();
        }
    }

    public void CleanRemove(int scaledBlocksPerIteration, Iterator<Block> iterator) {
        for (int i = 0; i < scaledBlocksPerIteration && iterator.hasNext(); i++) {
            Block block = iterator.next();
            currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Placing water blocks"));
            block.setType(Material.STATIONARY_WATER);
            removedBlocks.add(block); // Add the block to the new set

            // Remove the block from the main replacedBlocks set
            markedBlocks.remove(block);
        }
    }
}