package org.gecko.wauh.Listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Bed;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.gecko.wauh.Main;
import org.gecko.wauh.Listeners.BarrierListener;
import org.gecko.wauh.Listeners.BedrockListener;
import org.gecko.wauh.Listeners.WaterBucketListener;

import java.util.HashSet;
import java.util.Set;

public class BucketListener implements Listener {

    private final Set<Block> replacedBlocks = new HashSet<>();
    public Player currentRemovingPlayer;
    public boolean stopWaterRemoval = false;
    public boolean wauhRemovalActive = false;
    private int waterRemovedCount = 0;
    private int stationaryWaterRemovedCount = 0;
    private Set<Block> blocksToProcess = new HashSet<>();
    private Location clickedLocation;
    private boolean limitReached = false;
    private int highestDist = 0;
    private int dist;
    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        BarrierListener barrierListener = Main.getPlugin(Main.class).getBarrierListener();
        BedrockListener bedrockListener = Main.getPlugin(Main.class).getBedrockListener();
        WaterBucketListener waterBucketListener = Main.getPlugin(Main.class).getWaterBucketListener();
        if (!barrierListener.blockRemovalActive && !bedrockListener.allRemovalActive && !waterBucketListener.tsunamiActive) {
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
                    highestDist = 0;
                    blocksToProcess.clear();
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

    private void processWaterRemoval() {
        int radiusLimit = Main.getPlugin(Main.class).getRadiusLimit();
        int realRadiusLimit = radiusLimit + 2;
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
            dist = (int) clickedLocation.distance(block.getLocation());
            if (dist > radiusLimit) {
                limitReached = true;
                limitReachedThisIteration = true;
            }
            if (dist > highestDist) {
                int progressPercentage = (int) ((double) highestDist / (realRadiusLimit - 1) * 100);
                if (highestDist <= (radiusLimit)) {
                    highestDist = dist;
                    // Send a message to the player only when the dist value rises

                    if (highestDist < realRadiusLimit) {
                        currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Wauh removal: " + ChatColor.RED + progressPercentage + "% " + ChatColor.GREEN + "(" + ChatColor.RED + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                    } else if (!limitReachedThisIteration) {
                        currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Wauh removal: " + ChatColor.GREEN + progressPercentage + "% (" + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                    } else {
                        currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Wauh removal: " + ChatColor.GREEN + "100% " + "(" + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                    }
                } else {
                    limitReached = true;
                    limitReachedThisIteration = true;
                }
            }

            // Check if the block is water or stationary water
            if (block.getType() == Material.WATER) {
                waterRemovedCount++;
            } else if (block.getType() == Material.STATIONARY_WATER) {
                stationaryWaterRemovedCount++;
            }

            // Remove the water block
            block.setType(Material.STRUCTURE_VOID);

            // Add the block to the list of replaced blocks
            replacedBlocks.add(block);

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
        }

        blocksToProcess = nextSet;

        if (limitReachedThisIteration) {
            wauhFin();
        } else if (!blocksToProcess.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::processWaterRemoval, 2L);
        } else {
            currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Wauh removal: " + ChatColor.GREEN + "100% " + "(" + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
            wauhFin();
        }
    }

    private void wauhFin() {
        // Check if there are more blocks to process
        if (limitReached) {
            displaySummary();
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::removeReplacedBlocks, 20L);
        } else if (!blocksToProcess.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::processWaterRemoval, 2L);
        } else {
            displaySummary();
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::removeReplacedBlocks, 20L);
        }
    }

    public void displaySummary() {
        // Display the water removal summary to the player
        Player player = currentRemovingPlayer;
        if (waterRemovedCount + stationaryWaterRemovedCount > 1) {
            player.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + (waterRemovedCount + stationaryWaterRemovedCount) + ChatColor.GREEN + " wauh blocks.");

            // Display the water removal summary in the console
            Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GREEN + " removed " + ChatColor.RED + waterRemovedCount + ChatColor.GREEN + " updating water blocks and " + ChatColor.RED + stationaryWaterRemovedCount + ChatColor.GREEN + " stationary water blocks.");
        }
        wauhRemovalActive = false;
        currentRemovingPlayer = null;
    }

    private void removeReplacedBlocks() {
        for (Block block : replacedBlocks) {
            block.setType(Material.AIR);
        }
        replacedBlocks.clear();
    }
}
