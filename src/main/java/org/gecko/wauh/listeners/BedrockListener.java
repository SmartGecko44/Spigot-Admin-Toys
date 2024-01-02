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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.gecko.wauh.Main;
import org.gecko.wauh.data.ConfigurationManager;
import org.gecko.wauh.logic.Scale;

import java.util.*;

public class BedrockListener implements Listener {

    private static final Set<Material> IMMUTABLE_MATERIALS = EnumSet.of(Material.BEDROCK, Material.STATIONARY_WATER, Material.WATER, Material.LAVA, Material.STATIONARY_LAVA, Material.TNT);
    private final Set<Block> markedBlocks = new HashSet<>();
    private final Set<Block> processedBlocks = new HashSet<>();
    private final Set<Block> removedBlocks = new HashSet<>();
    private final Main plugin = new Main();
    private Player currentRemovingPlayer;
    private boolean stopAllRemoval = false;
    private boolean allRemovalActive = false;
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
    private String realSource = null;

    private void addIfValid(Block block, Set<Block> nextSet) {
        if (realSource.equalsIgnoreCase("TNT") || realSource.equalsIgnoreCase("creeper")) {
            if (!IMMUTABLE_MATERIALS.contains(block.getType())) {
                nextSet.add(block);
            } else if (block.getType() == Material.TNT) {
                Location location = block.getLocation();
                block.setType(Material.AIR);
                TNTPrimed tntPrimed = (TNTPrimed) location.getWorld().spawnEntity(location.add(0.5, 0.5, 0.5), EntityType.PRIMED_TNT);
                tntPrimed.setFuseTicks(20);
                nextSet.add(block);
            }
        } else if (!IMMUTABLE_MATERIALS.contains(block.getType()) && (block.getType() != Material.AIR)) {
            nextSet.add(block);
        } else if (block.getType() == Material.TNT) {
            Location location = block.getLocation();
            block.setType(Material.AIR);
            TNTPrimed tntPrimed = (TNTPrimed) location.getWorld().spawnEntity(location.add(0.5, 0.5, 0.5), EntityType.PRIMED_TNT);
            tntPrimed.setFuseTicks(20);
            nextSet.add(block);
        }
    }

    @EventHandler
    public void bedrockBreakEventHandler(BlockBreakEvent event) {
        bedrockValueAssignHandler(event, "player");
    }

    public void bedrockValueAssignHandler(BlockBreakEvent event, String source) {
        realSource = source;
        TNTListener tntListener = plugin.getTntListener();
        if (tntListener == null) {
            return;
        }
        if (event == null && source.equalsIgnoreCase("TNT") && (tntListener.getTntPlayer() != null && (!tntListener.getTntPlayer().isOp()))) {
            return;
        }
        if (event != null && (!event.getPlayer().isOp())) {
            return;
        }
        ConfigurationManager configManager;
        FileConfiguration config;
        configManager = new ConfigurationManager(plugin);
        config = configManager.getConfig();
        if (config.getInt("Bedrock enabled") == 0) {
            return;
        }
        BucketListener bucketListener = plugin.getBucketListener();
        BarrierListener barrierListener = plugin.getBarrierListener();
        WaterBucketListener waterBucketListener = plugin.getWaterBucketListener();
        CreeperListener creeperListener = plugin.getCreeperListener();
        if (source.equalsIgnoreCase("player")) {
            radiusLimit = plugin.getRadiusLimit();
        } else if (source.equalsIgnoreCase("TNT")) {
            radiusLimit = plugin.getTntRadiusLimit();
        } else {
            radiusLimit = plugin.getCreeperRadiusLimit();
        }
        realRadiusLimit = radiusLimit - 2;
        if (realRadiusLimit > 1 && (!bucketListener.isWauhRemovalActive() && !barrierListener.isBlockRemovalActive() && !isAllRemovalActive() && !waterBucketListener.isTsunamiActive() || explosionTrigger)) {
                if (event == null && source.equalsIgnoreCase("TNT") || source.equalsIgnoreCase("creeper")) {
                    setAllRemovalActive(true);
                    explosionTrigger = true;
                    limitReached = false;

                    if (tntListener.getTntLocation() != null) {
                        clickedLocation = tntListener.getTntLocation();
                    } else if (creeperListener != null && creeperListener.getCreeperLocation() != null) {
                        clickedLocation = creeperListener.getCreeperLocation();
                    } else {
                        return;
                    }

                    highestDist = 0;
                    allRemovedCount = 0;
                    blocksToProcess.clear();
                    if (tntListener.getTntPlayer() != null) {
                        setCurrentRemovingPlayer(tntListener.getTntPlayer());
                    } else {
                        setCurrentRemovingPlayer(null);
                    }

                    blocksToProcess.add(clickedLocation.getBlock());

                    processAllRemoval();
                } else if (event != null) {
                    if (event.getPlayer().getInventory().getItemInMainHand() == null || event.getPlayer().getInventory().getItemInMainHand().getAmount() == 0 || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
                        return;
                    }
                    Player player = event.getPlayer();
                    NBTItem nbtItem = new NBTItem(event.getPlayer().getInventory().getItemInMainHand());
                    String identifier = nbtItem.getString("Ident");
                    // Check if the bucket is filling with water
                    if (player.getInventory().getItemInMainHand().getType() == Material.BEDROCK && identifier.equalsIgnoreCase("Custom Bedrock") && (!IMMUTABLE_MATERIALS.contains(event.getBlock().getType()))) {
                        setAllRemovalActive(true);
                        limitReached = false;
                        clickedLocation = event.getBlock().getLocation();

                        // Reset the water removal counts and initialize the set of blocks to process
                        highestDist = 0;
                        allRemovedCount = 0;
                        blocksToProcess.clear();
                        setCurrentRemovingPlayer(player);

                        // Add the clicked block to the set of blocks to process
                        blocksToProcess.add(clickedLocation.getBlock());

                        // Start the water removal process
                        processAllRemoval();
                    }
                }

        }
    }

    private void processAllRemoval() {
        if (isStopAllRemoval()) {
            setStopAllRemoval(false);
            displaySummary();
            return;
        }
        Set<Block> nextSet = new HashSet<>();
        boolean limitReachedThisIteration = false; // Variable to track whether the limit was reached this iteration
        String aR = "All removal: ";
        for (Block block : blocksToProcess) {
            if (processedBlocks.contains(block)) {
                continue;
            }
            dist = (int) clickedLocation.distance(block.getLocation()) + 1;
            if (dist > radiusLimit - 3) {
                limitReached = true;
                limitReachedThisIteration = true;
            }
            if ((dist - 1) > highestDist && (dist > 1)) {
                int progressPercentage = (int) ((double) highestDist / (realRadiusLimit - 2) * 100);
                highestDist = dist - 1;
                if (getCurrentRemovingPlayer() != null) {
                    // Send a message to the player only when the dist value rises
                    if (highestDist < realRadiusLimit - 1) {
                        getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + aR + ChatColor.RED + progressPercentage + "% " + ChatColor.GREEN + "(" + ChatColor.RED + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                    } else if (!limitReachedThisIteration) {
                        getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + aR + ChatColor.GREEN + progressPercentage + "% (" + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                    } else {
                        getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + aR + ChatColor.GREEN + "100% " + "(" + realRadiusLimit + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                    }
                }
            }

            // Check if the block is grass or dirt
            allRemovedCount++;
            if (plugin.getShowRemoval()) {
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
            if (plugin.getShowRemoval()) {
                Bukkit.getScheduler().runTaskLater(plugin, this::processAllRemoval, 2L);
            } else {
                processAllRemoval();
            }
        } else {
            if (dist > 1 && getCurrentRemovingPlayer() != null) {
                getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + aR + ChatColor.GREEN + "100% " + "(" + realRadiusLimit + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
            }
            displaySummary();
        }
    }

    private void bedrockFin() {
        // Check if there are more blocks to process
        if (limitReached) {
            displaySummary();
        } else if (!blocksToProcess.isEmpty()) {
            if (plugin.getShowRemoval()) {
                Bukkit.getScheduler().runTaskLater(plugin, this::processAllRemoval, 2L);
            } else {
                processAllRemoval();
            }
        } else {
            displaySummary();
        }
    }

    public void displaySummary() {
        TNTListener tntListener = plugin.getTntListener();
        CreeperListener creeperListener = plugin.getCreeperListener();
        Player player = getCurrentRemovingPlayer();
        // Display the block removal summary to the player
        if (allRemovedCount > 1) {
            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + allRemovedCount + ChatColor.GREEN + " blocks.");
                // Display the block removal summary in the console
                Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GREEN + " removed " + ChatColor.RED + allRemovedCount + ChatColor.GREEN + " blocks.");
            }
            if (!plugin.getShowRemoval()) {
                removeMarkedBlocks();
            } else {
                clearAll(tntListener, creeperListener);
            }
        } else {
            clearAll(tntListener, creeperListener);
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
        TNTListener tntListener = plugin.getTntListener();
        CreeperListener creeperListener = plugin.getCreeperListener();
        Scale scale;
        scale = new Scale();

        int totalRemovedCount = allRemovedCount;
        if (totalRemovedCount < 50000) {
            for (Block block : markedBlocks) {
                block.setType(Material.AIR);
            }
            clearAll(tntListener, creeperListener);
        } else {
            scale.scaleReverseLogic(totalRemovedCount, radiusLimit, markedBlocks, "bedrock");
        }

        // If there are more blocks to remove, schedule the next batch
        if (!markedBlocks.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(plugin, this::removeMarkedBlocks, 10L); // Schedule the next batch after 1 tick
        } else if (!removedBlocks.isEmpty()) {
            if (repetitions > 0) {
                repetitions--;
                repeated = true;
                markedBlocks.addAll(removedBlocks);
                removedBlocks.clear();
                Bukkit.getScheduler().runTaskLater(plugin, this::removeMarkedBlocks, 100L);
                // If all blocks have been processed, but there are blocks in the removedBlocks set,
                // process those in the next iteration.
            } else {
                if (getCurrentRemovingPlayer() != null) {
                    getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Falling block cleanup finished"));
                }
                clearAll(tntListener, creeperListener);
            }
        }
    }

    private void clearAll(TNTListener tntListener, CreeperListener creeperListener) {
        setAllRemovalActive(false);
        explosionTrigger = false;
        setCurrentRemovingPlayer(null);
        clickedLocation = null;
        tntListener.setTntLocation(null);
        tntListener.setTntPlayer(null);
        creeperListener.setCreeperLocation(null);
        realSource = null;
        setStopAllRemoval(false);
        blocksToProcess.clear();
        markedBlocks.clear();
        processedBlocks.clear();
        removedBlocks.clear();
    }

    public void cleanRemove(int scaledBlocksPerIteration, Iterator<Block> iterator) {
        // Temporary list to store blocks to be removed
        List<Block> blocksToRemove = new ArrayList<>();
        for (int i = 0; i < scaledBlocksPerIteration && iterator.hasNext(); i++) {
            Block block = iterator.next();
            if (repeated) {
                if (getCurrentRemovingPlayer() != null) {
                    getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Cleaning up falling blocks"));
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

    public Player getCurrentRemovingPlayer() {
        return currentRemovingPlayer;
    }

    public void setCurrentRemovingPlayer(Player currentRemovingPlayer) {
        this.currentRemovingPlayer = currentRemovingPlayer;
    }

    public boolean isStopAllRemoval() {
        return stopAllRemoval;
    }

    public void setStopAllRemoval(boolean stopAllRemoval) {
        this.stopAllRemoval = stopAllRemoval;
    }

    public boolean isAllRemovalActive() {
        return allRemovalActive;
    }

    public void setAllRemovalActive(boolean allRemovalActive) {
        this.allRemovalActive = allRemovalActive;
    }
}