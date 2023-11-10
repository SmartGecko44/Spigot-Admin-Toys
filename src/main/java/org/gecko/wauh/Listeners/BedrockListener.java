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
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;

public class BedrockListener implements Listener {

    public Player currentRemovingPlayer;
    public boolean stopAllRemoval = false;
    public boolean allRemovalActive = false;
    private int allRemovedCount;
    private Set<Block> blocksToProcess = new HashSet<>();
    private final Set<Block> markedBlocks = new HashSet<>();
    private Location clickedLocation;
    private boolean limitReached = false;
    private int highestDist = 0;
    private int dist;
    private int radiusLimit;
    private int realRadiusLimit;
    private final Set<Block> processedBlocks = new HashSet<>();

    @EventHandler
    public void BedrockClick(BlockBreakEvent event) {
        BucketListener bucketListener = Main.getPlugin(Main.class).getBucketListener();
        BarrierListener barrierListener = Main.getPlugin(Main.class).getBarrierListener();
        WaterBucketListener waterBucketListener = Main.getPlugin(Main.class).getWaterBucketListener();
        radiusLimit = Main.getPlugin(Main.class).getRadiusLimit();
        realRadiusLimit = radiusLimit - 2;
        if (realRadiusLimit > 1) {
            if (!bucketListener.wauhRemovalActive && !barrierListener.blockRemovalActive && !allRemovalActive && !waterBucketListener.tsunamiActive) {
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
    }

    private void processAllRemoval() {
        if (stopAllRemoval) {
            stopAllRemoval = false;
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
                        currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "All removal: " + ChatColor.RED + progressPercentage + "% " + ChatColor.GREEN + "(" + ChatColor.RED + highestDist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                    } else if (!limitReachedThisIteration) {
                        currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "All removal: " + ChatColor.GREEN + progressPercentage + "% (" + highestDist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                    } else {
                        currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "All removal: " + ChatColor.GREEN + "100% " + "(" + highestDist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                    }
                }
            }

            // Check if the block is grass or dirt
            allRemovedCount++;
            if (Main.getPlugin(Main.class).getShowRemoval()) {
                // if (block.getType() != Material.DIAMOND_ORE) {
                    // block.setType(Material.AIR);
                // } else {
                    block.setType(Material.AIR);
                // }
            } else {
                markedBlocks.add(block);
            }

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
            processedBlocks.add(block);
        }

        blocksToProcess = nextSet;

        if (limitReachedThisIteration) {
            bedrockFin();
        } else if (!blocksToProcess.isEmpty()) {
            if (Main.getPlugin(Main.class).getShowRemoval()) {
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::processAllRemoval, 2L);
            } else {
                processAllRemoval();
            }
        } else {
            if (dist > 1) {
                currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "All removal: " + ChatColor.GREEN + "100% " + "(" + highestDist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
            }
            displaySummary();
        }
    }

    private void bedrockFin() {
        // Check if there are more blocks to process
        if (limitReached) {
            displaySummary();
        } else if(!blocksToProcess.isEmpty()){
            if (Main.getPlugin(Main.class).getShowRemoval()) {
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::processAllRemoval, 2L);
            } else {
                processAllRemoval();
            }
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
            if (!Main.getPlugin(Main.class).getShowRemoval()) {
                removeMarkedBlocks();
            } else {
                allRemovalActive = false;
                currentRemovingPlayer = null;
                stopAllRemoval = false;
                blocksToProcess.clear();
                markedBlocks.clear();
                processedBlocks.clear();
                removedBlocks.clear();
            }
        } else {
            allRemovalActive = false;
            currentRemovingPlayer = null;
            stopAllRemoval = false;
            blocksToProcess.clear();
            markedBlocks.clear();
            processedBlocks.clear();
            removedBlocks.clear();
        }
    }
    private final Set<Block> removedBlocks = new HashSet<>();
    private int repetitions = 2;
    private boolean repeated = false;

    private void removeMarkedBlocks() {
        int totalRemovedCount = allRemovedCount;
        if (totalRemovedCount < 50000 && radiusLimit < 50) {
            for (Block block : markedBlocks) {
                block.setType(Material.AIR);
            }
            allRemovalActive = false;
            currentRemovingPlayer = null;
            stopAllRemoval = false;
            blocksToProcess.clear();
            markedBlocks.clear();
            processedBlocks.clear();
            removedBlocks.clear();
        } else {
            // Set BLOCKS_PER_ITERATION dynamically based on the total count
            //TODO: Fix this stuff
            int sqrtTotalBlocks = (int) (Math.sqrt(totalRemovedCount) * radiusLimit) / (2 ^ (int) Math.sqrt(radiusLimit));
            int scaledBlocksPerIteration = Math.max(1, sqrtTotalBlocks);
            // Update BLOCKS_PER_ITERATION based on the scaled value

            List<Block> reversedBlocks = new ArrayList<>(markedBlocks);
            Collections.reverse(reversedBlocks); // Reverse the order of blocks

            Iterator<Block> iterator = reversedBlocks.iterator();

            for (int i = 0; i < scaledBlocksPerIteration && iterator.hasNext(); i++) {
                Block block = iterator.next();
                if (repeated) {
                    if (block.getType() == Material.SAND || block.getType() == Material.GRAVEL) {
                        block.setType(Material.AIR);
                        removedBlocks.add(block); // Add the block to the new set

                        // Remove the block from the main replacedBlocks set
                        markedBlocks.remove(block);
                    } else {
                        markedBlocks.remove(block);
                    }
                } else {
                    block.setType(Material.AIR);
                    removedBlocks.add(block); // Add the block to the new set

                    // Remove the block from the main replacedBlocks set
                    markedBlocks.remove(block);
                }
            }
        }

        // If there are more blocks to remove, schedule the next batch
        if (!markedBlocks.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::removeMarkedBlocks, 10L); // Schedule the next batch after 1 tick
        } else if (!removedBlocks.isEmpty()) {
            if (repetitions > 0) {
                repetitions--;
                repeated = true;
                markedBlocks.addAll(removedBlocks);
                removedBlocks.clear();
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::removeMarkedBlocks, 100L);
                // If all blocks have been processed, but there are blocks in the removedBlocks set,
                // process those in the next iteration.
            } else {
                allRemovalActive = false;
                currentRemovingPlayer = null;
                stopAllRemoval = false;
                blocksToProcess.clear();
                markedBlocks.clear();
                processedBlocks.clear();
                removedBlocks.clear();
            }
        }
    }
}