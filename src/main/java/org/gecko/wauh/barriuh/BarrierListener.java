package org.gecko.wauh.barriuh;

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
import org.bukkit.event.block.BlockBreakEvent;
import org.gecko.wauh.Main;

import java.util.HashSet;
import java.util.Set;

public class BarrierListener implements Listener, CommandExecutor {

    private int grassRemovedCount = 0;
    private int dirtRemovedCount = 0;
    private Set<Block> blocksToProcess = new HashSet<>();
    private Player currentRemovingPlayer;
    private boolean stopBlockRemoval = false;
    private Location clickedLocation;
    private boolean limitReached = false;
    private int highestDist = 0;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (sender instanceof Player) {
            Player player = (Player) sender;
            stopBlockRemoval = true;
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Water removal" + ChatColor.RED + "stopped.");
            displaySummary();
        }
        return true;
    }

    @EventHandler
    public void BarrierClick(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (event.getBlock().getType() == Material.GRASS || event.getBlock().getType() == Material.DIRT) {
            // Check if the bucket is filling with water
            if (player.getInventory().getItemInMainHand().getType() == Material.BARRIER) {
                limitReached = false;
                event.getBlock().setType(Material.BEDROCK);
                clickedLocation = event.getBlock().getLocation();

                // Reset the water removal counts and initialize the set of blocks to process
                grassRemovedCount = 0;
                dirtRemovedCount = 0;
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
            if (dist > (radiusLimit)) {
                limitReached = true;
                limitReachedThisIteration = true;
            }
            if (dist > highestDist) {
                highestDist = dist;
                // Send a message to the player only when the dist value rises
                currentRemovingPlayer.sendMessage(dist + "/" + realRadiusLimit);
            }

            // Check if the block is grass or dirt
            if (block.getType() == Material.GRASS) {
                grassRemovedCount++;
            } else if (block.getType() == Material.DIRT) {
                dirtRemovedCount++;
            }

            block.setType(Material.AIR);

            // Iterate through neighboring blocks and add them to the next set
            for (BlockFace face : BlockFace.values()) {
                Block neighboringBlock = block.getRelative(face);
                if ((neighboringBlock.getType() == Material.GRASS || neighboringBlock.getType() == Material.DIRT)) {
                    nextSet.add(neighboringBlock);
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

    private void displaySummary() {
        // Display the block removal summary to the player
        Player player = currentRemovingPlayer;
        player.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + grassRemovedCount + ChatColor.GREEN + " grass blocks and " + ChatColor.RED + dirtRemovedCount + ChatColor.GREEN + " dirt blocks.");

        // Display the block removal summary in the console
        Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GREEN + " removed " + ChatColor.RED + grassRemovedCount + ChatColor.GREEN + " grass blocks and " + ChatColor.RED + dirtRemovedCount + ChatColor.GREEN + " dirt blocks.");
    }
}
