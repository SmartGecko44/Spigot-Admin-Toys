package org.gecko.wauh.wauhbuck;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.gecko.wauh.Main;

import java.util.HashSet;
import java.util.Set;

public class BucketListener implements Listener, CommandExecutor {

    private int waterRemovedCount = 0;
    private int stationaryWaterRemovedCount = 0;
    private Set<Block> blocksToProcess = new HashSet<>();
    private Player currentRemovingPlayer;
    private final Set<Block> replacedBlocks = new HashSet<>();
    private boolean stopWaterRemoval = false;
    private Location clickedLocation;
    private boolean limitReached = false;
    private int highestDist = 0;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            stopWaterRemoval = true;
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Water removal" + ChatColor.RED + " stopped.");
            displaySummary();
        }
        return true;
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        // Check if the bucket is filling with water
        if (event.getBlockClicked().getType() == Material.WATER || event.getBlockClicked().getType() == Material.STATIONARY_WATER) {
            if (event.getBucket() == Material.BUCKET) {
                limitReached = false;
                event.getBlockClicked().setType(Material.BEDROCK);
                Player player = event.getPlayer();
                clickedLocation = event.getBlockClicked().getLocation();

                // Reset the water removal counts and initialize the set of blocks to process
                waterRemovedCount = 0;
                stationaryWaterRemovedCount = 0;
                blocksToProcess.clear();
                currentRemovingPlayer = player;
                replacedBlocks.clear();

                // Add the clicked block to the set of blocks to process
                blocksToProcess.add(clickedLocation.getBlock());

                replacedBlocks.add(clickedLocation.getBlock());

                // Start the water removal process
                processWaterRemoval();
            }
        }
    }

    private void processWaterRemoval() {
        int radiusLimit = Main.getPlugin(Main.class).getRadiusLimit();
        int realRadiusLimit = radiusLimit - 2;
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
            int dist = (int) clickedLocation.distance(block.getLocation());
            if (dist > (radiusLimit)) {
                limitReached = true;
                limitReachedThisIteration = true;
            }
            if (dist > highestDist) {
                highestDist = dist;
                // Send a message to the player only when the dist value rises
                currentRemovingPlayer.sendMessage(dist + "/" + realRadiusLimit);
            }

            // Check if the block is water or stationary water
            if (block.getType() == Material.WATER) {
                waterRemovedCount++;
            } else if (block.getType() == Material.STATIONARY_WATER) {
                stationaryWaterRemovedCount++;
            }

            // Remove the water block
            block.setType(Material.BARRIER);

            // Add the block to the list of replaced blocks
            replacedBlocks.add(block);

            // Iterate through neighboring blocks and add them to the next set
            for (BlockFace face : BlockFace.values()) {
                Block neighboringBlock = block.getRelative(face);
                if ((neighboringBlock.getType() == Material.WATER || neighboringBlock.getType() == Material.STATIONARY_WATER)) {
                    nextSet.add(neighboringBlock);
                }
            }
        }

        blocksToProcess = nextSet;

        if (limitReachedThisIteration) {
            wauhFin();
        } else if (!blocksToProcess.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::processWaterRemoval, 2L);
        } else {
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
        }
    }

    private void displaySummary() {
        // Display the water removal summary to the player
        Player player = currentRemovingPlayer;
        player.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + waterRemovedCount + ChatColor.GREEN + " wauh blocks.");

        // Display the water removal summary in the console
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GREEN + " removed " + ChatColor.RED + waterRemovedCount + ChatColor.GREEN + " flowing water blocks and " + ChatColor.RED + stationaryWaterRemovedCount + ChatColor.GREEN + " stationary water blocks.");
    }

    private void removeReplacedBlocks() {
        for (Block block : replacedBlocks) {
            block.setType(Material.AIR);
        }
        replacedBlocks.clear();
    }
}
