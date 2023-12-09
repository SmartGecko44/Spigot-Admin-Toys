package org.gecko.wauh.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.gecko.wauh.Main;
import org.gecko.wauh.data.ConfigurationManager;
import org.gecko.wauh.logic.ScaleReverse;

import java.util.*;

public class BarrierListener implements Listener {

    private final Set<Block> markedBlocks = new HashSet<>();
    private final Set<Block> processedBlocks = new HashSet<>();
    private final Set<Block> removedBlocks = new HashSet<>();
    public Player currentRemovingPlayer;
    public boolean stopBlockRemoval = false;
    public boolean blockRemovalActive = false;
    private int grassRemovedCount;
    private int dirtRemovedCount;
    private int barrierRemovedCount;
    private Set<Block> blocksToProcess = new HashSet<>();
    private Location clickedLocation;
    private boolean limitReached = false;
    private int highestDist = 0;
    private int dist;
    private int radiusLimit;
    private int realRadiusLimit;
    private static final Set<Material> IMMUTABLE_MATERIALS = EnumSet.of(Material.GRASS, Material.DIRT, Material.BARRIER, Material.STRUCTURE_VOID);

    private void addIfValid(Block block, Set<Block> nextSet) {
        if (IMMUTABLE_MATERIALS.contains(block.getType())) {
            nextSet.add(block);
        }
    }

    @EventHandler
    public void barrierBreakEventHandler(BlockBreakEvent event) {
        ConfigurationManager configManager;
        FileConfiguration config;
        configManager = new ConfigurationManager(Main.getPlugin(Main.class));
        config = configManager.getConfig();
        if (config.getInt("Barrier enabled") == 0) {
            return;
        }
        BucketListener bucketListener = Main.getPlugin(Main.class).getBucketListener();
        BedrockListener bedrockListener = Main.getPlugin(Main.class).getBedrockListener();
        WaterBucketListener waterBucketListener = Main.getPlugin(Main.class).getWaterBucketListener();
        radiusLimit = Main.getPlugin(Main.class).getRadiusLimit();
        realRadiusLimit = radiusLimit - 2;
        if (realRadiusLimit > 1) {
            if (!bucketListener.wauhRemovalActive && !blockRemovalActive && !bedrockListener.allRemovalActive && !waterBucketListener.tsunamiActive) {
                Player player = event.getPlayer();
                if (IMMUTABLE_MATERIALS.contains(event.getBlock().getType())) {
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
        }
    }

    private void processBlockRemoval() {
        if (stopBlockRemoval) {
            stopBlockRemoval = false;
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
                        currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Block removal: " + ChatColor.RED + progressPercentage + "% " + ChatColor.GREEN + "(" + ChatColor.RED + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                    } else if (!limitReachedThisIteration) {
                        currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Block removal: " + ChatColor.GREEN + progressPercentage + "% (" + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                    } else {
                        currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Block removal: " + ChatColor.GREEN + "100% " + "(" + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                    }
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
            if (!Main.getPlugin(Main.class).getShowRemoval()) {
                markedBlocks.add(block);
            } else {
                block.setType(Material.AIR);
            }

            // Iterate through neighboring blocks and add them to the next set
            // Iterate through neighboring blocks and add them to the next set
            for (int i = -1; i <= 1; i++) {
                if (i == 0) continue; // Skip the current block
                addIfValid(block.getRelative(i, 0, 0), nextSet);
                addIfValid(block.getRelative(0, i, 0), nextSet);
                addIfValid(block.getRelative(0, 0, i), nextSet);
            }
            processedBlocks.add(block);
        }

        blocksToProcess = nextSet;

        if (limitReachedThisIteration) {
            barriuhFin();
        } else if (!blocksToProcess.isEmpty()) {
            if (Main.getPlugin(Main.class).getShowRemoval()) {
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::processBlockRemoval, 1L);
            } else {
                processBlockRemoval();
            }
        } else {
            if (dist > 1) {
                currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Block removal: " + ChatColor.GREEN + "100% " + "(" + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
            }
            displaySummary();
        }
    }

    private void barriuhFin() {
        // Check if there are more blocks to process
        if (limitReached) {
            displaySummary();
        } else if (!blocksToProcess.isEmpty()) {
            if (Main.getPlugin(Main.class).getShowRemoval()) {
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::processBlockRemoval, 1L);
            } else {
                processBlockRemoval();
            }
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
            if (!Main.getPlugin(Main.class).getShowRemoval()) {
                removeMarkedBlocks();
            } else {
                blockRemovalActive = false;
                currentRemovingPlayer = null;
                stopBlockRemoval = false;
                blocksToProcess.clear();
                markedBlocks.clear();
                processedBlocks.clear();
                removedBlocks.clear();
            }
        } else {
            blockRemovalActive = false;
            currentRemovingPlayer = null;
            stopBlockRemoval = false;
            blocksToProcess.clear();
            markedBlocks.clear();
            processedBlocks.clear();
            removedBlocks.clear();
        }
    }

    private void removeMarkedBlocks() {
        ScaleReverse scaleReverse;
        scaleReverse = new ScaleReverse();

        int totalRemovedCount = dirtRemovedCount + grassRemovedCount + barrierRemovedCount;
        if (totalRemovedCount < 50000) {
            for (Block block : markedBlocks) {
                block.setType(Material.AIR);
            }
            blockRemovalActive = false;
            currentRemovingPlayer = null;
            stopBlockRemoval = false;
            blocksToProcess.clear();
            markedBlocks.clear();
            processedBlocks.clear();
            removedBlocks.clear();
        } else {
            scaleReverse.ScaleReverseLogic(totalRemovedCount, radiusLimit, markedBlocks, "barrier");

            // If there are more blocks to remove, schedule the next batch
            if (!markedBlocks.isEmpty()) {
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::removeMarkedBlocks, 10L); // Schedule the next batch after 1 tick
            } else if (!removedBlocks.isEmpty()) {
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::removeMarkedBlocks, 100L);
                // If all blocks have been processed, but there are blocks in the removedBlocks set,
                // process those in the next iteration.
                blockRemovalActive = false;
                currentRemovingPlayer = null;
                stopBlockRemoval = false;
                blocksToProcess.clear();
                markedBlocks.clear();
                processedBlocks.clear();
                removedBlocks.clear();
            }
        }
    }

    public void CleanRemove(int scaledBlocksPerIteration, Iterator<Block> iterator) {
        List<Block> blocksToRemove = new ArrayList<>();
        for (int i = 0; i < scaledBlocksPerIteration && iterator.hasNext(); i++) {
            Block block = iterator.next();
            block.setType(Material.AIR);
            // Add the block to the new set
            removedBlocks.add(block);
            // Add the block to the temporary list for removal later
            blocksToRemove.add(block);
        }

        // Remove all blocks from markedBlocks that are in the temporary list
        for (Block block : blocksToRemove) {
            markedBlocks.remove(block);
        }
    }
}