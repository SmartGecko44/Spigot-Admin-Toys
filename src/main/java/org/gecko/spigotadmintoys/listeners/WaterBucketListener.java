package org.gecko.spigotadmintoys.listeners;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
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
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.spigotadmintoys.Main;
import org.gecko.spigotadmintoys.logic.IterateBlocks;
import org.gecko.spigotadmintoys.logic.Scale;
import org.gecko.spigotadmintoys.logic.SetAndGet;

import java.util.*;
import java.util.function.Function;

public class WaterBucketListener implements Listener {

    private static final Set<Material> IMMUTABLE_MATERIALS = EnumSet.of(Material.AIR);
    private final SetAndGet setAndGet;
    private final Set<Block> markedBlocks = new HashSet<>();
    private final Set<Block> processedBlocks = new HashSet<>();
    private final Set<Block> removedBlocks = new HashSet<>();
    private Player currentRemovingPlayer;
    private boolean stopTsunami = false;
    private boolean tsunamiActive = false;
    private int waterPlacedCount;
    private Set<Block> blocksToProcess = new HashSet<>();
    private Location clickedLocation;
    private boolean limitReached = false;
    private int highestDist = 0;
    private int dist;
    private int radiusLimit;
    private int realRadiusLimit;
    private boolean showRemoval;

    public WaterBucketListener(SetAndGet setAndGet) {
        this.setAndGet = setAndGet;
    }

    @EventHandler
    public void tsunamiClick(PlayerBucketEmptyEvent event) {
        if (!event.getPlayer().isOp() || event.getPlayer().getInventory().getItemInMainHand() == null || event.getPlayer().getInventory().getItemInMainHand().getAmount() == 0 || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
            return;
        }

        FileConfiguration config = setAndGet.getConfigManager().getConfig();

        if (config.getInt("Tsunami enabled") == 0) {
            return;
        }

        BucketListener bucketListener = setAndGet.getBucketListener();
        BarrierListener barrierListener = setAndGet.getBarrierListener();
        BedrockListener bedrockListener = setAndGet.getBedrockListener();
        String identifier = NBT.get(event.getPlayer().getInventory().getItemInMainHand(), (Function<ReadableItemNBT, String>) nbt -> nbt.getString("Ident"));
        radiusLimit = setAndGet.getRadiusLimit();
        realRadiusLimit = radiusLimit - 2;
        showRemoval = setAndGet.getShowRemoval();
        if (realRadiusLimit > 1 && !bucketListener.isWauhRemovalActive() && !barrierListener.isBlockRemovalActive() && !bedrockListener.isAllRemovalActive() && !isTsunamiActive()) {
            Player player = event.getPlayer();
            // Check if the bucket is filling with water
            if (player.getInventory().getItemInMainHand().getType() == Material.WATER_BUCKET && identifier.equalsIgnoreCase("Custom Tsunami") && player.isSneaking()) {
                setTsunamiActive(true);
                limitReached = false;
                clickedLocation = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();

                // Reset the water removal counts and initialize the set of blocks to process
                highestDist = 0;
                waterPlacedCount = 0;
                blocksToProcess.clear();
                setCurrentRemovingPlayer(player);

                // Add the clicked block to the set of blocks to process
                blocksToProcess.add(clickedLocation.getBlock());

                // Start the water removal process
                processTsunami();
            }
        }
    }

    private void processTsunami() {
        if (isStopTsunami()) {
            setStopTsunami(false);
            displaySummary();
            return;
        }
        Set<Block> nextSet = new HashSet<>();
        boolean limitReachedThisIteration = false; // Variable to track whether the limit was reached this iteration
        String tsunami = "Tsunami: ";
        IterateBlocks iterateBlocks = setAndGet.getIterateBlocks();
        for (Block block : blocksToProcess) {
            if (processedBlocks.contains(block)) {
                continue;
            }
            dist = (int) clickedLocation.distance(block.getLocation()) + 1;
            if (dist > radiusLimit - 3) {
                limitReached = true;
                limitReachedThisIteration = true;
            }
            if ((dist - 1) > highestDist && dist > 1) {
                int progressPercentage = (int) ((double) highestDist / (realRadiusLimit - 2) * 100);
                highestDist = dist - 1;
                // Send a message to the player only when the dist value rises
                if (highestDist < realRadiusLimit - 1) {
                    getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + tsunami + ChatColor.RED + progressPercentage + "% " + ChatColor.GREEN + "(" + ChatColor.RED + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                } else if (!limitReachedThisIteration) {
                    getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + tsunami + ChatColor.GREEN + progressPercentage + "% (" + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                } else {
                    getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + tsunami + ChatColor.GREEN + "100% " + "(" + realRadiusLimit + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
                }
            }

            waterPlacedCount++;
            if (block.getType() == Material.AIR) {
                if (showRemoval) {
                    block.setType(Material.WATER);
                } else {
                    markedBlocks.add(block);
                }
            }

            // Iterate through neighboring blocks and add them to the next set
            for (int i = -1; i <= 1; i++) {
                if (i == 0) continue; // Skip the current block
                iterateBlocks.iterateBlocks(block, nextSet, IMMUTABLE_MATERIALS, false);
            }
            processedBlocks.add(block);
        }

        blocksToProcess = nextSet;

        if (limitReachedThisIteration) {
            tsunamiFin();
        } else if (!blocksToProcess.isEmpty()) {
            if (showRemoval) {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), this::processTsunami, 2L);
            } else {
                processTsunami();
            }
        } else {
            if (dist > 0) {
                getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + tsunami + ChatColor.GREEN + "100% " + "(" + realRadiusLimit + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")"));
            }
            displaySummary();
        }
    }

    private void tsunamiFin() {
        // Check if there are more blocks to process
        if (limitReached) {
            displaySummary();
        } else if (!blocksToProcess.isEmpty()) {
            if (showRemoval) {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), this::processTsunami, 2L);
            } else {
                processTsunami();
            }
        } else {
            displaySummary();
        }
    }

    public void displaySummary() {
        Player player = getCurrentRemovingPlayer();
        // Display the block removal summary to the player
        if (waterPlacedCount > 1) {
            player.sendMessage(ChatColor.GREEN + "Placed " + ChatColor.RED + waterPlacedCount + ChatColor.GREEN + " water blocks.");
            // Display the block removal summary in the console
            Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GREEN + " placed " + ChatColor.RED + waterPlacedCount + ChatColor.GREEN + " water blocks.");
            if (!showRemoval) {
                removeMarkedBlocks();
            } else {
                clear();
            }
        } else {
            clear();
        }
    }

    private void removeMarkedBlocks() {
        Scale scale = setAndGet.getScale();

        int totalRemovedCount = waterPlacedCount;
        if (totalRemovedCount < 50000) {
            for (Block block : markedBlocks) {
                block.setType(Material.STATIONARY_WATER);
            }
            clear();
            return;
        } else {
            scale.scaleReverseLogic(totalRemovedCount, radiusLimit, markedBlocks, "wauh", this::cleanRemove);
        }

        // If there are more blocks to remove, schedule the next batch
        if (!markedBlocks.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), this::removeMarkedBlocks, 1L); // Schedule the next batch after 1 tick
        } else if (!removedBlocks.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), this::removeMarkedBlocks, 100L);
            // If all blocks have been processed, but there are blocks in the removedBlocks set,
            // process those in the next iteration.
            getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Water placement finished"));
            clear();
        }
    }

    private void clear() {
        setTsunamiActive(false);
        setCurrentRemovingPlayer(null);
        setStopTsunami(false);
        blocksToProcess.clear();
        markedBlocks.clear();
        processedBlocks.clear();
        removedBlocks.clear();
    }

    public void cleanRemove(int scaledBlocksPerIteration, Iterator<Block> iterator) {
        List<Block> blocksToRemove = new ArrayList<>();
        for (int i = 0; i < scaledBlocksPerIteration && iterator.hasNext(); i++) {
            Block block = iterator.next();
            getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
                    new TextComponent(ChatColor.RED + "Placing water blocks, " + markedBlocks.size() + " blocks left. That's " + (markedBlocks.size() / scaledBlocksPerIteration + 1) + (markedBlocks.size() / scaledBlocksPerIteration == 1 ? " iteration" : " iterations") + " left"));
            block.setType(Material.STATIONARY_WATER);
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

    public boolean isStopTsunami() {
        return stopTsunami;
    }

    public void setStopTsunami(boolean stopTsunami) {
        this.stopTsunami = stopTsunami;
    }

    public boolean isTsunamiActive() {
        return tsunamiActive;
    }

    public void setTsunamiActive(boolean tsunamiActive) {
        this.tsunamiActive = tsunamiActive;
    }
}