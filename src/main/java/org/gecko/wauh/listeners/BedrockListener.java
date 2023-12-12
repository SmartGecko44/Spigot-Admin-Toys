package org.gecko.wauh.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.gecko.wauh.Main;
import org.gecko.wauh.data.ConfigurationManager;
import org.gecko.wauh.logic.ScaleReverse;

import java.util.*;

public class BedrockListener implements Listener {

    private static final Set<Material> IMMUTABLE_MATERIALS = EnumSet.of(Material.AIR, Material.BEDROCK, Material.STATIONARY_WATER, Material.WATER, Material.LAVA, Material.STATIONARY_LAVA, Material.TNT);
    private static final Set<Material> ORES = EnumSet.of(Material.COAL_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.GOLD_ORE, Material.IRON_ORE, Material.LAPIS_ORE, Material.QUARTZ_ORE, Material.REDSTONE_ORE);
    private final Set<Block> markedBlocks = new HashSet<>();
    private final Set<Block> processedBlocks = new HashSet<>();
    private final Set<Block> removedBlocks = new HashSet<>();
    private final Main mainPlugin = Main.getPlugin(Main.class);
    public Player currentRemovingPlayer;
    public boolean stopAllRemoval = false;
    public boolean allRemovalActive = false;
    private int allRemovedCount;
    private Set<Block> blocksToProcess = new HashSet<>();
    private Location clickedLocation;
    private boolean limitReached = false;
    private int highestDist = 0;
    private int dist;
    private int radiusLimit;
    private int realRadiusLimit;
    private int repetitions = 3;
    private boolean repeated = false;
    private boolean explosionTrigger = false;

    private void addIfValid(Block block, Set<Block> nextSet) {
        if (!IMMUTABLE_MATERIALS.contains(block.getType())) {
            nextSet.add(block);
        } else if (block.getType() == Material.TNT) {
            Location location = block.getLocation();
            block.setType(Material.AIR);
            TNTPrimed tntPrimed = (TNTPrimed) location.getWorld().spawnEntity(location.add(0.5, 0.5, 0.5), EntityType.PRIMED_TNT);
            tntPrimed.setFuseTicks(40);
            nextSet.add(block);
        }
    }

    @EventHandler
    public void bedrockBreakEventHandler(BlockBreakEvent event) {
        bedrockValueAssignHandler(event, "player");
    }

    public void bedrockValueAssignHandler(BlockBreakEvent event, String source) {
        TNTListener tntListener = mainPlugin.getTntListener();
        if (tntListener == null) {
            return;
        }
        if (!event.getPlayer().isOp() || tntListener.tntPlayer.isOp()) {
            return;
        }
        ConfigurationManager configManager;
        FileConfiguration config;
        configManager = new ConfigurationManager(Main.getPlugin(Main.class));
        config = configManager.getConfig();
        if (config.getInt("Bedrock enabled") == 0) {
            return;
        }
        BucketListener bucketListener = mainPlugin.getBucketListener();
        BarrierListener barrierListener = mainPlugin.getBarrierListener();
        WaterBucketListener waterBucketListener = mainPlugin.getWaterBucketListener();
        CreeperListener creeperListener = mainPlugin.getCreeperListener();
        if (source.equalsIgnoreCase("player")) {
            radiusLimit = mainPlugin.getRadiusLimit();
        } else if (source.equalsIgnoreCase("TNT")) {
            radiusLimit = mainPlugin.getTntRadiusLimit();
        } else {
            radiusLimit = mainPlugin.getCreeperRadiusLimit();
        }
        realRadiusLimit = radiusLimit - 2;
        if (realRadiusLimit > 1) {
            if (!bucketListener.wauhRemovalActive && !barrierListener.blockRemovalActive && !allRemovalActive && !waterBucketListener.tsunamiActive || explosionTrigger) {
                if (source.equalsIgnoreCase("TNT") || source.equalsIgnoreCase("creeper")) {
                    allRemovalActive = true;
                    explosionTrigger = true;
                    limitReached = false;

                    if (tntListener.tntLocation != null) {
                        clickedLocation = tntListener.tntLocation;
                    } else if (creeperListener != null && creeperListener.creeperLocation != null) {
                        clickedLocation = creeperListener.creeperLocation;
                    } else {
                        return;
                    }

                    highestDist = 0;
                    allRemovedCount = 0;
                    blocksToProcess.clear();
                    if (tntListener.tntPlayer != null) {
                        currentRemovingPlayer = tntListener.tntPlayer;
                    } else {
                        currentRemovingPlayer = null;
                    }

                    blocksToProcess.add(clickedLocation.getBlock());

                    processAllRemoval();
                } else {
                    Player player = event.getPlayer();
                    // Check if the bucket is filling with water
                    if (player.getInventory().getItemInMainHand().getType() == Material.BEDROCK) {
                        if (!IMMUTABLE_MATERIALS.contains(event.getBlock().getType())) {
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
                    if (currentRemovingPlayer != null) {
                        // Send a message to the player only when the dist value rises
                        if (highestDist < realRadiusLimit - 1) {
                            currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "All removal: " + ChatColor.RED + progressPercentage + "% " + ChatColor.GREEN + "(" + ChatColor.RED + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                        } else if (!limitReachedThisIteration) {
                            currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "All removal: " + ChatColor.GREEN + progressPercentage + "% (" + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                        } else {
                            currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "All removal: " + ChatColor.GREEN + "100% " + "(" + realRadiusLimit + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                        }
                    }
                }
            }

            // Check if the block is grass or dirt
            allRemovedCount++;
            if (Main.getPlugin(Main.class).getShowRemoval()) {
                block.setType(Material.AIR);
            } else {
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
            bedrockFin();
        } else if (!blocksToProcess.isEmpty()) {
            if (Main.getPlugin(Main.class).getShowRemoval()) {
                Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::processAllRemoval, 2L);
            } else {
                processAllRemoval();
            }
        } else {
            if (dist > 1 && currentRemovingPlayer != null) {
                currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "All removal: " + ChatColor.GREEN + "100% " + "(" + realRadiusLimit + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
            }
            displaySummary();
        }
    }

    private void bedrockFin() {
        // Check if there are more blocks to process
        if (limitReached) {
            displaySummary();
        } else if (!blocksToProcess.isEmpty()) {
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
        TNTListener tntListener = mainPlugin.getTntListener();
        CreeperListener creeperListener = mainPlugin.getCreeperListener();
        Player player = currentRemovingPlayer;
        // Display the block removal summary to the player
        if (allRemovedCount > 1) {
            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + allRemovedCount + ChatColor.GREEN + " blocks.");
                // Display the block removal summary in the console
                Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GREEN + " removed " + ChatColor.RED + allRemovedCount + ChatColor.GREEN + " blocks.");
            }
            if (!Main.getPlugin(Main.class).getShowRemoval()) {
                removeMarkedBlocks();
            } else {
                allRemovalActive = false;
                explosionTrigger = false;
                currentRemovingPlayer = null;
                clickedLocation = null;
                tntListener.tntLocation = null;
                tntListener.tntPlayer = null;
                creeperListener.creeperLocation = null;
                stopAllRemoval = false;
                blocksToProcess.clear();
                markedBlocks.clear();
                processedBlocks.clear();
                removedBlocks.clear();
            }
        } else {
            allRemovalActive = false;
            explosionTrigger = false;
            currentRemovingPlayer = null;
            clickedLocation = null;
            tntListener.tntLocation = null;
            tntListener.tntPlayer = null;
            creeperListener.creeperLocation = null;
            stopAllRemoval = false;
            blocksToProcess.clear();
            markedBlocks.clear();
            processedBlocks.clear();
            removedBlocks.clear();
        }
    }

    /**
     * Remove the marked blocks.
     * If the total removed count is less than 50000, remove all the marked blocks.
     * Otherwise, remove the marked blocks in batches based on the scaled value of BLOCKS_PER_ITERATION.
     * If there are more blocks to remove, schedule the next batch.
     * If all blocks have been processed but there are blocks in the removedBlocks set,
     * process those in the next iteration. If repetitions are greater than 0, repeat the process.
     * Finally, clear all the sets and variables related to block removal.
     */
    private void removeMarkedBlocks() {
        TNTListener tntListener = mainPlugin.getTntListener();
        CreeperListener creeperListener = mainPlugin.getCreeperListener();
        ScaleReverse scaleReverse;
        scaleReverse = new ScaleReverse();

        int totalRemovedCount = allRemovedCount;
        if (totalRemovedCount < 50000) {
            for (Block block : markedBlocks) {
                block.setType(Material.AIR);
            }
            allRemovalActive = false;
            explosionTrigger = false;
            currentRemovingPlayer = null;
            clickedLocation = null;
            tntListener.tntLocation = null;
            tntListener.tntPlayer = null;
            creeperListener.creeperLocation = null;
            stopAllRemoval = false;
            blocksToProcess.clear();
            markedBlocks.clear();
            processedBlocks.clear();
            removedBlocks.clear();
        } else {
            scaleReverse.ScaleReverseLogic(totalRemovedCount, radiusLimit, markedBlocks, "bedrock");
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
                if (currentRemovingPlayer != null) {
                    currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Falling block cleanup finished"));
                }
                allRemovalActive = false;
                explosionTrigger = false;
                currentRemovingPlayer = null;
                clickedLocation = null;
                tntListener.tntLocation = null;
                tntListener.tntPlayer = null;
                creeperListener.creeperLocation = null;
                stopAllRemoval = false;
                blocksToProcess.clear();
                markedBlocks.clear();
                processedBlocks.clear();
                removedBlocks.clear();
            }
        }
    }

    public void CleanRemove(int scaledBlocksPerIteration, Iterator<Block> iterator) {
        // Temporary list to store blocks to be removed
        List<Block> blocksToRemove = new ArrayList<>();
        for (int i = 0; i < scaledBlocksPerIteration && iterator.hasNext(); i++) {
            Block block = iterator.next();
            if (repeated) {
                if (currentRemovingPlayer != null) {
                    currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Cleaning up falling blocks"));
                }
                if (block.getType() == Material.SAND || block.getType() == Material.GRAVEL) {
                    block.setType(Material.AIR);
                    // Add the block to the new set
                    removedBlocks.add(block);

                    // Add the block to temporary list
                    blocksToRemove.add(block);
                } else {
                    // Add the block to temporary list
                    blocksToRemove.add(block);
                }
            } else {
                block.setType(Material.AIR);
                // Add the block to the new set
                removedBlocks.add(block);

                // Add the block to temporary list
                blocksToRemove.add(block);
            }
        }

        // Remove all blocks from markedBlocks that are in the temporary list
        for (Block block : blocksToRemove) {
            markedBlocks.remove(block);
        }
    }
}