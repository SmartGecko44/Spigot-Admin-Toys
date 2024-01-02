package org.gecko.wauh.listeners;

import de.tr7zw.changeme.nbtapi.NBTItem;
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
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.gecko.wauh.Main;
import org.gecko.wauh.data.ConfigurationManager;
import org.gecko.wauh.logic.Scale;

import java.util.*;

public class BucketListener implements Listener {

    private final Set<Block> markedBlocks = new HashSet<>();
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
    private static final Set<Material> IMMUTABLE_MATERIALS = EnumSet.of(Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA);

    private void addIfValid(Block block, Set<Block> nextSet) {
        if (IMMUTABLE_MATERIALS.contains(block.getType())) {
            nextSet.add(block);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (!event.getPlayer().isOp() || event.getPlayer().getInventory().getItemInMainHand() == null || event.getPlayer().getInventory().getItemInMainHand().getAmount() == 0 || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
            return;
        }
        ConfigurationManager configManager;
        FileConfiguration config;
        configManager = new ConfigurationManager(Main.getPlugin(Main.class));
        config = configManager.getConfig();
        if (config.getInt("Bucket enabled") == 0) {
            return;
        }
        BarrierListener barrierListener = Main.getPlugin(Main.class).getBarrierListener();
        BedrockListener bedrockListener = Main.getPlugin(Main.class).getBedrockListener();
        WaterBucketListener waterBucketListener = Main.getPlugin(Main.class).getWaterBucketListener();
        NBTItem nbtItem = new NBTItem(event.getPlayer().getInventory().getItemInMainHand());
        String identifier = nbtItem.getString("Ident");
        radiusLimit = Main.getPlugin(Main.class).getRadiusLimit();
        realRadiusLimit = radiusLimit - 2;
        if (realRadiusLimit > 1 && !wauhRemovalActive && !barrierListener.blockRemovalActive && !bedrockListener.allRemovalActive && !waterBucketListener.tsunamiActive && IMMUTABLE_MATERIALS.contains(event.getBlockClicked().getType()) && event.getBucket() == Material.BUCKET && identifier.equalsIgnoreCase("Custom Bucket")) {
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

            markedBlocks.add(event.getBlockClicked());

            // Start the water removal process
            processWaterRemoval();
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
                markedBlocks.add(block);
            } else if (!Main.getPlugin(Main.class).getShowRemoval()) {
                markedBlocks.add(block);
            }

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
            markedBlocks.clear();
            processedBlocks.clear();
        }
    }

    private void removeReplacedBlocks() {
        Scale scale;
        scale = new Scale();

        // Add this variable
        int totalRemovedCount = waterRemovedCount + stationaryWaterRemovedCount + lave;
        if (totalRemovedCount < 50000) {
            for (Block block : markedBlocks) {
                block.setType(Material.AIR);
            }
            wauhRemovalActive = false;
            currentRemovingPlayer = null;
            stopWaterRemoval = false;
            blocksToProcess.clear();
            markedBlocks.clear();
            processedBlocks.clear();
            return;
        } else {
            scale.ScaleReverseLogic(totalRemovedCount, radiusLimit, markedBlocks, "bucket");
        }

        // If there are more blocks to remove, schedule the next batch
            if (!markedBlocks.isEmpty()) {
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::removeReplacedBlocks, 1L); // Schedule the next batch after 1 tick
            } else if (!removedBlocks.isEmpty()) {
                // If all blocks have been processed, but there are blocks in the removedBlocks set,
                // process those in the next iteration.
                if (!Main.getPlugin(Main.class).getShowRemoval()) {
                    if (repetitions > 0) {
                        repetitions--;
                        markedBlocks.addAll(removedBlocks);
                        removedBlocks.clear();
                        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::removeReplacedBlocks, 1L);
                    } else {
                        currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Water block cleanup finished"));
                        // Reset repetitions to stop further repetitions
                        repetitions = 1;
                        wauhRemovalActive = false;
                        currentRemovingPlayer = null;
                        stopWaterRemoval = false;
                        blocksToProcess.clear();
                        markedBlocks.clear();
                        processedBlocks.clear();
                    }

                } else {
                    currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Water block cleanup finished"));
                    repetitions = 1;
                    wauhRemovalActive = false;
                    currentRemovingPlayer = null;
                    stopWaterRemoval = false;
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
            currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Cleaning up water"));
            // Change the block to air
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