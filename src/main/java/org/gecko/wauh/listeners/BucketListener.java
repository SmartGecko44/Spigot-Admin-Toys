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
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.gecko.wauh.Main;

import java.util.*;

public class BucketListener implements Listener {

    private final Set<Block> replacedBlocks = new HashSet<>();
    private final Set<Block> processedBlocks = new HashSet<>();
    private final Set<Block> removedBlocks = new HashSet<>(); // Create a new set to store removed blocks
    public Player currentRemovingPlayer;
    public boolean stopWaterRemoval = false;
    public boolean wauhRemovalActive = false;
    private int waterRemovedCount = 0;
    private int stationaryWaterRemovedCount = 0;
    private int lave = 0;
    private Set<Block> blocksToProcess = new HashSet<>();
    private Location clickedLocation;
    private boolean limitReached = false;
    private int highestDist = 0;
    private int radiusLimit;
    private int realRadiusLimit;
    private int repetitions = 1;

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        BarrierListener barrierListener = Main.getPlugin(Main.class).getBarrierListener();
        BedrockListener bedrockListener = Main.getPlugin(Main.class).getBedrockListener();
        WaterBucketListener waterBucketListener = Main.getPlugin(Main.class).getWaterBucketListener();
        radiusLimit = Main.getPlugin(Main.class).getRadiusLimit();
        realRadiusLimit = radiusLimit - 2;
        if (realRadiusLimit > 1) {
            if (!wauhRemovalActive && !barrierListener.blockRemovalActive && !bedrockListener.allRemovalActive && !waterBucketListener.tsunamiActive) {
                // Check if the bucket is filling with water
                if (event.getBlockClicked().getType() == Material.WATER || event.getBlockClicked().getType() == Material.STATIONARY_WATER || event.getBlockClicked().getType() == Material.LAVA || event.getBlockClicked().getType() == Material.STATIONARY_LAVA) {
                    if (event.getBucket() == Material.BUCKET) {
                        wauhRemovalActive = true;
                        Player player = event.getPlayer();

                        limitReached = false;
                        clickedLocation = event.getBlockClicked().getLocation();

                        // Reset the water removal counts and initialize the set of blocks to process
                        waterRemovedCount = 0;
                        stationaryWaterRemovedCount = 0;
                        lave = 0;
                        highestDist = 0;
                        blocksToProcess.clear();
                        processedBlocks.clear();
                        currentRemovingPlayer = player;

                        // Add the clicked block to the set of blocks to process
                        blocksToProcess.add(event.getBlockClicked());

                        replacedBlocks.add(event.getBlockClicked());

                        // Start the water removal process
                        processWaterRemoval();
                    }
                }
            }
        }
    }

    private void processWaterRemoval() {
        if (stopWaterRemoval) {
            stopWaterRemoval = false;
            displaySummary();
            // Schedule a task to remove the barrier blocks after a short delay
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::removeReplacedBlocks, 20L); // Delay the removal of barrier blocks for 1 second (20 ticks)
            return;
        }
        Set<Block> nextSet = new HashSet<>();
        boolean limitReachedThisIteration = false; // Variable to track whether the limit was reached this iteration
        for (Block block : blocksToProcess) {
            if (processedBlocks.contains(block)) {
                continue;
            }
            int dist = (int) clickedLocation.distance(block.getLocation()) + 1;
            if (dist > radiusLimit - 3) {
                limitReached = true;
                limitReachedThisIteration = true;
            }
            if ((dist - 1) > highestDist) {
                int progressPercentage = (int) ((double) highestDist / (realRadiusLimit - 2) * 100);
                highestDist = dist - 1;
                // Send a message to the player only when the dist value rises

                if (highestDist < realRadiusLimit - 1) {
                    currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Wauh removal: " + ChatColor.RED + progressPercentage + "% " + ChatColor.GREEN + "(" + ChatColor.RED + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                } else if (!limitReachedThisIteration) {
                    currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Wauh removal: " + ChatColor.GREEN + progressPercentage + "% (" + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                } else {
                    currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Wauh removal: " + ChatColor.GREEN + "100% " + "(" + realRadiusLimit + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                }
            }

            // Check if the block is water or stationary water
            if (block.getType() == Material.WATER) {
                waterRemovedCount++;
            } else if (block.getType() == Material.STATIONARY_WATER) {
                stationaryWaterRemovedCount++;
            } else if (block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA) {
                lave++;
            }
            // Remove the water block
            if (Main.getPlugin(Main.class).getShowRemoval()) {
                block.setType(Material.STRUCTURE_VOID);
                // Add the block to the list of replaced blocks
                replacedBlocks.add(block);
            } else if (!Main.getPlugin(Main.class).getShowRemoval()) {
                replacedBlocks.add(block);
            }

            // Iterate through neighboring blocks and add them to the next set
            for (int i = -1; i <= 1; i++) {
                if (i == 0) continue; // Skip the current block
                Block neighboringBlockX = block.getRelative(i, 0, 0);
                Block neighboringBlockY = block.getRelative(0, i, 0);
                Block neighboringBlockZ = block.getRelative(0, 0, i);

                if ((neighboringBlockX.getType() == Material.WATER || neighboringBlockX.getType() == Material.STATIONARY_WATER || neighboringBlockX.getType() == Material.LAVA || neighboringBlockX.getType() == Material.STATIONARY_LAVA)) {
                    nextSet.add(neighboringBlockX);
                }
                if ((neighboringBlockY.getType() == Material.WATER || neighboringBlockY.getType() == Material.STATIONARY_WATER || neighboringBlockY.getType() == Material.LAVA || neighboringBlockY.getType() == Material.STATIONARY_LAVA)) {
                    nextSet.add(neighboringBlockY);
                }
                if ((neighboringBlockZ.getType() == Material.WATER || neighboringBlockZ.getType() == Material.STATIONARY_WATER || neighboringBlockZ.getType() == Material.LAVA || neighboringBlockZ.getType() == Material.STATIONARY_LAVA)) {
                    nextSet.add(neighboringBlockZ);
                }
            }
            processedBlocks.add(block);
        }

        blocksToProcess = nextSet;


        if (limitReachedThisIteration) {
            wauhFin();
        } else if (!blocksToProcess.isEmpty()) {
            if (Main.getPlugin(Main.class).getShowRemoval()) {
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::processWaterRemoval, 1L);
            } else {
                processWaterRemoval();
            }
        } else {
            currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Wauh removal: " + ChatColor.GREEN + "100% " + "(" + realRadiusLimit + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
            wauhFin();
        }
    }

    private void wauhFin() {
        // Check if there are more blocks to process
        if (limitReached) {
            displaySummary();
        } else if (!blocksToProcess.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::processWaterRemoval, 1L);
        } else {
            displaySummary();
        }
    }

    public void displaySummary() {
        // Display the water removal summary to the player
        Player player = currentRemovingPlayer;
        if (waterRemovedCount + stationaryWaterRemovedCount + lave > 1) {
            player.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + (waterRemovedCount + stationaryWaterRemovedCount + lave) + ChatColor.GREEN + " wauh blocks.");

            // Display the water removal summary in the console
            Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GREEN + " removed " + ChatColor.RED + waterRemovedCount + ChatColor.GREEN + " updating water blocks, " + ChatColor.RED + stationaryWaterRemovedCount + ChatColor.GREEN + " stationary water blocks and " + ChatColor.RED + lave + ChatColor.GREEN + " lave blocks.");
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::removeReplacedBlocks, 20L);
        } else {
            wauhRemovalActive = false;
            currentRemovingPlayer = null;
            stopWaterRemoval = false;
            blocksToProcess.clear();
            replacedBlocks.clear();
            processedBlocks.clear();
        }
    }

    private void removeReplacedBlocks() {
        // Add this variable
        int totalRemovedCount = waterRemovedCount + stationaryWaterRemovedCount + lave;
        if (totalRemovedCount < 50000 && radiusLimit < 50) {
            for (Block block : replacedBlocks) {
                block.setType(Material.AIR);
            }
            wauhRemovalActive = false;
            currentRemovingPlayer = null;
            stopWaterRemoval = false;
            blocksToProcess.clear();
            replacedBlocks.clear();
            processedBlocks.clear();
        } else {
            // Set BLOCKS_PER_ITERATION dynamically based on the total count
            //TODO: Fix this stuff
            int sqrtTotalBlocks = (int) (Math.sqrt((totalRemovedCount)) * radiusLimit) / (2 ^ (int) Math.sqrt(radiusLimit));
            int scaledBlocksPerIteration = Math.max(1, sqrtTotalBlocks);
            // Update BLOCKS_PER_ITERATION based on the scaled value

            List<Block> reversedBlocks = new ArrayList<>(replacedBlocks);
            Collections.reverse(reversedBlocks); // Reverse the order of blocks

            Iterator<Block> iterator = reversedBlocks.iterator();

            for (int i = 0; i < scaledBlocksPerIteration && iterator.hasNext(); i++) {
                Block block = iterator.next();
                currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Cleaning up water"));
                // Add debug output to indicate that a block is being removed
                block.setType(Material.AIR);
                removedBlocks.add(block); // Add the block to the new set

                // Remove the block from the main replacedBlocks set
                replacedBlocks.remove(block);
            }

            // If there are more blocks to remove, schedule the next batch
            if (!replacedBlocks.isEmpty()) {
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::removeReplacedBlocks, 1L); // Schedule the next batch after 1 tick
            } else if (!removedBlocks.isEmpty()) {
                // If all blocks have been processed, but there are blocks in the removedBlocks set,
                // process those in the next iteration.
                if (!Main.getPlugin(Main.class).getShowRemoval()) {
                    if (repetitions < 2) { // Repeat only twice
                        repetitions++;
                        replacedBlocks.addAll(removedBlocks);
                        removedBlocks.clear();
                        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::removeReplacedBlocks, 1L);
                    } else {
                        currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Water block cleanup finished"));
                        // Reset repetitions to stop further repetitions
                        repetitions = 0;
                        wauhRemovalActive = false;
                        currentRemovingPlayer = null;
                        stopWaterRemoval = false;
                        blocksToProcess.clear();
                        replacedBlocks.clear();
                        processedBlocks.clear();
                    }

                } else {
                    currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Water block cleanup finished"));
                    repetitions = 0;
                    wauhRemovalActive = false;
                    currentRemovingPlayer = null;
                    stopWaterRemoval = false;
                    blocksToProcess.clear();
                    replacedBlocks.clear();
                    processedBlocks.clear();
                }
            }
        }
    }
}