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
    private final Main plugin;
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

    public BedrockListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void bedrockBreakEventHandler(BlockBreakEvent event) {
        bedrockValueAssignHandler(event, "player");
    }

    public void bedrockValueAssignHandler(BlockBreakEvent event, String source) {
        realSource = source;
        TNTListener tntListener = plugin.getTntListener();
        if (tntListener == null || isInvalidEvent(event, source, tntListener)) {
            return;
        }

        FileConfiguration config = new ConfigurationManager(plugin).getConfig();
        if (config.getInt("Bedrock enabled") == 0) {
            return;
        }

        radiusLimit = getRadiusLimit(source);
        realRadiusLimit = radiusLimit - 2;

        if (isRemovalPossible()) {
            initializeRemoval(source, event, tntListener);
        }
    }

    private boolean isInvalidEvent(BlockBreakEvent event, String source, TNTListener tntListener) {
        return (event == null && source.equalsIgnoreCase("TNT") && (tntListener.getTntPlayer() != null && (!tntListener.getTntPlayer().isOp()))) ||
                (event != null && (!event.getPlayer().isOp()));
    }

    private int getRadiusLimit(String source) {
        if (source.equalsIgnoreCase("player")) {
            return plugin.getRadiusLimit();
        } else if (source.equalsIgnoreCase("TNT")) {
            return plugin.getTntRadiusLimit();
        } else {
            return plugin.getCreeperRadiusLimit();
        }
    }

    private boolean isRemovalPossible() {
        return realRadiusLimit > 1 && (!plugin.getBucketListener().isWauhRemovalActive() && !plugin.getBarrierListener().isBlockRemovalActive() && !isAllRemovalActive() && !plugin.getWaterBucketListener().isTsunamiActive() || explosionTrigger);
    }

    private void initializeRemoval(String source, BlockBreakEvent event, TNTListener tntListener) {
        if (event == null && source.equalsIgnoreCase("TNT") || source.equalsIgnoreCase("creeper")) {
            setAllRemovalActive(true);
            explosionTrigger = true;
            limitReached = false;

            clickedLocation = getClickedLocation(tntListener, plugin.getCreeperListener());
            if (clickedLocation == null) {
                return;
            }

            highestDist = 0;
            allRemovedCount = 0;
            blocksToProcess.clear();
            setCurrentRemovingPlayer(tntListener.getTntPlayer());

            blocksToProcess.add(clickedLocation.getBlock());

            processAllRemoval();
        } else if (event != null) {
            handlePlayerEvent(event);
        }
    }

    private Location getClickedLocation(TNTListener tntListener, CreeperListener creeperListener) {
        if (tntListener.getTntLocation() != null) {
            return tntListener.getTntLocation();
        } else if (creeperListener != null && creeperListener.getCreeperLocation() != null) {
            return creeperListener.getCreeperLocation();
        } else {
            return null;
        }
    }

    private void handlePlayerEvent(BlockBreakEvent event) {
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

    private void processAllRemoval() {
        if (isStopAllRemoval()) {
            setStopAllRemoval(false);
            displaySummary();
            return;
        }

        Set<Block> nextSet = new HashSet<>();
        boolean limitReachedThisIteration = false;

        for (Block block : blocksToProcess) {
            if (processedBlocks.contains(block)) {
                continue;
            }

            dist = (int) clickedLocation.distance(block.getLocation()) + 1;
            if (dist > radiusLimit - 3) {
                limitReached = true;
                limitReachedThisIteration = true;
            }

            updateProgress();
            removeBlockAndAddNeighbors(block, nextSet);
        }

        blocksToProcess = nextSet;

        if (limitReachedThisIteration) {
            bedrockFin();
        } else if (!blocksToProcess.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(plugin, this::processAllRemoval, 2L);
        } else {
            displaySummary();
        }
    }

    private void updateProgress() {
        if ((dist - 1) > highestDist && (dist > 1)) {
            highestDist = dist - 1;
            int progressPercentage = (int) ((double) highestDist / (realRadiusLimit - 2) * 100);
            sendProgressMessage(progressPercentage);
        }
    }

    private void sendProgressMessage(int progressPercentage) {
        if (getCurrentRemovingPlayer() != null) {
            String aR = "All removal: ";
            String progressMessage = ChatColor.GREEN + aR + ChatColor.RED + progressPercentage + "% " + ChatColor.GREEN + "(" + ChatColor.RED + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realRadiusLimit + ")";
            getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(progressMessage));
        }
    }

    private void removeBlockAndAddNeighbors(Block block, Set<Block> nextSet) {
        allRemovedCount++;
        if (plugin.getShowRemoval()) {
            block.setType(Material.AIR);
        } else {
            markedBlocks.add(block);
        }

        for (int i = -1; i <= 1; i++) {
            if (i == 0) continue;
            addIfValid(block.getRelative(i, 0, 0), nextSet);
            addIfValid(block.getRelative(0, i, 0), nextSet);
            addIfValid(block.getRelative(0, 0, i), nextSet);
        }

        processedBlocks.add(block);
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