package org.gecko.spigotadmintoys.listeners;

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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.spigotadmintoys.Main;
import org.gecko.spigotadmintoys.data.ConfigurationManager;
import org.gecko.spigotadmintoys.logic.Scale;
import org.gecko.spigotadmintoys.logic.SetAndGet;

import java.util.*;

public class BarrierListener implements Listener {

    private static final Set<Material> IMMUTABLE_MATERIALS = EnumSet.of(Material.GRASS, Material.DIRT, Material.BARRIER, Material.STRUCTURE_VOID);
    private final SetAndGet setAndGet;
    private final Set<Block> markedBlocks = new HashSet<>();
    private final Set<Block> processedBlocks = new HashSet<>();
    private final Set<Block> removedBlocks = new HashSet<>();
    private Player currentRemovingPlayer;
    private boolean stopBlockRemoval = false;
    private boolean blockRemovalActive = false;
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
    private boolean showRemoval;

    public BarrierListener(SetAndGet setAndGet) {
        this.setAndGet = setAndGet;
    }

    private void addIfValid(Block block, Set<Block> nextSet) {
        if (IMMUTABLE_MATERIALS.contains(block.getType())) {
            nextSet.add(block);
        }
    }

    @EventHandler
    public void barrierBreakEventHandler(BlockBreakEvent event) {
        if (!event.getPlayer().isOp() || event.getPlayer().getInventory().getItemInMainHand() == null || event.getPlayer().getInventory().getItemInMainHand().getAmount() == 0 || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
            return;
        }
        ConfigurationManager configManager;
        FileConfiguration config;
        configManager = setAndGet.getConfigManager();
        config = configManager.getConfig();
        if (config.getInt("Barrier enabled") == 0) {
            return;
        }
        BucketListener bucketListener = setAndGet.getBucketListener();
        BedrockListener bedrockListener = setAndGet.getBedrockListener();
        WaterBucketListener waterBucketListener = setAndGet.getWaterBucketListener();
        NBTItem nbtItem = new NBTItem(event.getPlayer().getInventory().getItemInMainHand());
        String identifier = nbtItem.getString("Ident");
        radiusLimit = setAndGet.getRadiusLimit();
        realRadiusLimit = radiusLimit - 2;
        showRemoval = setAndGet.getShowRemoval();
        if (realRadiusLimit > 1 && (!bucketListener.isWauhRemovalActive() && !isBlockRemovalActive() && !bedrockListener.isAllRemovalActive() && !waterBucketListener.isTsunamiActive())) {
            Player player = event.getPlayer();
            if (IMMUTABLE_MATERIALS.contains(event.getBlock().getType()) && player.getInventory().getItemInMainHand().getType() == Material.BARRIER && identifier.equalsIgnoreCase("Custom Barrier")) {
                setBlockRemovalActive(true);
                limitReached = false;
                clickedLocation = event.getBlock().getLocation();

                // Reset the water removal counts and initialize the set of blocks to process
                grassRemovedCount = 0;
                dirtRemovedCount = 0;
                barrierRemovedCount = 0;
                highestDist = 0;
                blocksToProcess.clear();
                setCurrentRemovingPlayer(player);

                // Add the clicked block to the set of blocks to process
                blocksToProcess.add(clickedLocation.getBlock());

                // Start the water removal process
                processBlockRemoval();
            }
        }
    }

    private void processBlockRemoval() {
        if (isStopBlockRemoval()) {
            setStopBlockRemoval(false);
            displaySummary();
            return;
        }
        Set<Block> nextSet = new HashSet<>();
        boolean limitReachedThisIteration = false; // Variable to track whether the limit was reached this iteration
        String bR = "Block removal: ";
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
                // Send a message to the player only when the dist value rises
                if (highestDist < realRadiusLimit - 1) {
                    getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + bR + ChatColor.RED + progressPercentage + "% " + ChatColor.GREEN + "(" + ChatColor.RED + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                } else if (!limitReachedThisIteration) {
                    getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + bR + ChatColor.GREEN + progressPercentage + "% (" + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                } else {
                    getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + bR + ChatColor.GREEN + "100% " + "(" + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
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
            if (!showRemoval) {
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
            if (showRemoval) {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), this::processBlockRemoval, 1L);
            } else {
                processBlockRemoval();
            }
        } else {
            if (dist > 1) {
                getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + bR + ChatColor.GREEN + "100% " + "(" + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
            }
            displaySummary();
        }
    }

    private void barriuhFin() {
        // Check if there are more blocks to process
        if (limitReached) {
            displaySummary();
        } else if (!blocksToProcess.isEmpty()) {
            if (showRemoval) {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), this::processBlockRemoval, 1L);
            } else {
                processBlockRemoval();
            }
        } else {
            displaySummary();
        }
    }

    public void displaySummary() {
        Player player = getCurrentRemovingPlayer();
        // Display the block removal summary to the player
        if (grassRemovedCount + dirtRemovedCount + barrierRemovedCount > 1) {
            String removed = "Removed ";
            String dA = " dirt blocks and ";
            String bB = " barrier blocks.";
            if (barrierRemovedCount == 0 && grassRemovedCount == 0 && dirtRemovedCount > 0) {
                player.sendMessage(ChatColor.GREEN + removed + ChatColor.RED + dirtRemovedCount + ChatColor.GREEN + " dirt blocks.");
            } else if (barrierRemovedCount == 0 && dirtRemovedCount == 0 && grassRemovedCount > 0) {
                player.sendMessage(ChatColor.GREEN + removed + ChatColor.RED + grassRemovedCount + ChatColor.GREEN + " grass blocks.");
            } else if (barrierRemovedCount == 0 && grassRemovedCount > 0 && dirtRemovedCount > 0) {
                player.sendMessage(ChatColor.GREEN + removed + ChatColor.RED + grassRemovedCount + ChatColor.GREEN + " grass blocks and " + ChatColor.RED + dirtRemovedCount + ChatColor.GREEN + " dirt blocks.");
            } else if (barrierRemovedCount > 0 && grassRemovedCount > 0 && dirtRemovedCount > 0) {
                player.sendMessage(ChatColor.GREEN + removed + ChatColor.RED + grassRemovedCount + ChatColor.GREEN + " grass blocks, " + ChatColor.RED + dirtRemovedCount + ChatColor.GREEN + dA + ChatColor.RED + barrierRemovedCount + ChatColor.GREEN + bB);
            } else if (barrierRemovedCount > 0 && grassRemovedCount > 0 && dirtRemovedCount == 0) {
                player.sendMessage(ChatColor.GREEN + removed + ChatColor.RED + grassRemovedCount + ChatColor.GREEN + " grass blocks and " + ChatColor.RED + barrierRemovedCount + ChatColor.GREEN + bB);
            } else if (barrierRemovedCount > 0 && grassRemovedCount == 0 && dirtRemovedCount > 0) {
                player.sendMessage(ChatColor.GREEN + removed + ChatColor.RED + dirtRemovedCount + ChatColor.GREEN + dA + ChatColor.RED + barrierRemovedCount + ChatColor.GREEN + bB);
            }
            // Display the block removal summary in the console
            Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GREEN + " removed " + ChatColor.RED + grassRemovedCount + ChatColor.GREEN + " grass blocks, " + ChatColor.RED + dirtRemovedCount + ChatColor.GREEN + dA + ChatColor.RED + barrierRemovedCount + ChatColor.GREEN + bB);
            if (!showRemoval) {
                removeMarkedBlocks();
            } else {
                cleanup();
            }
        } else {
            cleanup();
        }
    }

    private void removeMarkedBlocks() {
        Scale scale = setAndGet.getScale();

        int totalRemovedCount = dirtRemovedCount + grassRemovedCount + barrierRemovedCount;
        if (totalRemovedCount < 50000) {
            for (Block block : markedBlocks) {
                block.setType(Material.AIR);
            }
            cleanup();
        } else {
            scale.scaleReverseLogic(totalRemovedCount, radiusLimit, markedBlocks, "barrier");

            // If there are more blocks to remove, schedule the next batch
            if (!markedBlocks.isEmpty()) {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), this::removeMarkedBlocks, 10L); // Schedule the next batch after 1 tick
            } else if (!removedBlocks.isEmpty()) {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), this::removeMarkedBlocks, 100L);
                // If all blocks have been processed, but there are blocks in the removedBlocks set,
                // process those in the next iteration.
                cleanup();
            }
        }
    }

    private void cleanup() {
        setBlockRemovalActive(false);
        setCurrentRemovingPlayer(null);
        setStopBlockRemoval(false);
        blocksToProcess.clear();
        markedBlocks.clear();
        processedBlocks.clear();
        removedBlocks.clear();
    }

    public void cleanRemove(int scaledBlocksPerIteration, Iterator<Block> iterator) {
        List<Block> blocksToRemove = new ArrayList<>();
        for (int i = 0; i < scaledBlocksPerIteration && iterator.hasNext(); i++) {
            Block block = iterator.next();
            getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Cleaning up surface blocks, " + markedBlocks.size() + " blocks left. That's " + (markedBlocks.size() / scaledBlocksPerIteration + 1) + (markedBlocks.size() / scaledBlocksPerIteration == 1 ? " iteration" : " iterations") + " left"));
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

    public Player getCurrentRemovingPlayer() {
        return currentRemovingPlayer;
    }

    public void setCurrentRemovingPlayer(Player currentRemovingPlayer) {
        this.currentRemovingPlayer = currentRemovingPlayer;
    }

    public boolean isStopBlockRemoval() {
        return stopBlockRemoval;
    }

    public void setStopBlockRemoval(boolean stopBlockRemoval) {
        this.stopBlockRemoval = stopBlockRemoval;
    }

    public boolean isBlockRemovalActive() {
        return blockRemovalActive;
    }

    public void setBlockRemovalActive(boolean blockRemovalActive) {
        this.blockRemovalActive = blockRemovalActive;
    }
}