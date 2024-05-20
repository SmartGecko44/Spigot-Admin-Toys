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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.spigotadmintoys.Main;
import org.gecko.spigotadmintoys.data.ConfigurationManager;
import org.gecko.spigotadmintoys.logic.Scale;
import org.gecko.spigotadmintoys.logic.SetAndGet;

import java.util.*;

public class BedrockListener implements Listener {

    public static final String SOURCE = "Source";
    private static final Set<Material> IMMUTABLE_MATERIALS = EnumSet.of(Material.BEDROCK, Material.STATIONARY_WATER, Material.WATER, Material.LAVA, Material.STATIONARY_LAVA, Material.TNT);
    private final Set<Block> markedBlocks = new HashSet<>();
    private final Set<Block> processedBlocks = new HashSet<>();
    private final Set<Block> removedBlocks = new HashSet<>();
    private final SetAndGet setAndGet;
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
    private boolean showRemoval;
    private TNTListener tntListener;
    private CreeperListener creeperListener;

    public BedrockListener(SetAndGet setAndGet) {
        this.setAndGet = setAndGet;
    }

    private void addIfValid(Block block, Set<Block> nextSet) {
        if (realSource.equalsIgnoreCase("TNT") || realSource.equalsIgnoreCase("creeper")) {
            if (!IMMUTABLE_MATERIALS.contains(block.getType())) {
                nextSet.add(block);
            } else if (block.getType() == Material.TNT) {
                Location location = block.getLocation();
                block.setType(Material.AIR);
                TNTPrimed tntPrimed = (TNTPrimed) location.getWorld().spawnEntity(location.add(0.5, 0.5, 0.5), EntityType.PRIMED_TNT);
                tntPrimed.setFuseTicks(20);
                if (tntListener.getTntPlayer() != null) {
                tntPrimed.setMetadata(SOURCE, new FixedMetadataValue(JavaPlugin.getPlugin(Main.class), tntListener.getTntPlayer().getName()));
                }
                nextSet.add(block);
            }
        } else if (!IMMUTABLE_MATERIALS.contains(block.getType()) && block.getType() != Material.AIR && !markedBlocks.contains(block)) {
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
        tntListener = this.setAndGet.getTntListener();
        if (event == null && !source.equalsIgnoreCase("TNT") && !source.equalsIgnoreCase("Creeper") && !(tntListener.getTntPlayer() == null || tntListener.getTnt().getMetadata(SOURCE).getFirst().asString() == null)) {
            return;
        }
        ConfigurationManager configManager;
        FileConfiguration config;
        configManager = new ConfigurationManager(JavaPlugin.getPlugin(Main.class));
        config = configManager.getConfig();
        if (config.getInt("Bedrock enabled") == 0) {
            return;
        }
        BucketListener bucketListener = setAndGet.getBucketListener();
        BarrierListener barrierListener = setAndGet.getBarrierListener();
        WaterBucketListener waterBucketListener = setAndGet.getWaterBucketListener();
        creeperListener = setAndGet.getCreeperListener();
        showRemoval = setAndGet.getShowRemoval();

        if (source.equalsIgnoreCase("player")) {
            radiusLimit = setAndGet.getRadiusLimit();
        } else if (source.equalsIgnoreCase("TNT")) {
            radiusLimit = setAndGet.getTntRadiusLimit();
        } else {
            radiusLimit = setAndGet.getCreeperRadiusLimit();
        }
        realRadiusLimit = radiusLimit - 2;
        if (realRadiusLimit > 1 && (!bucketListener.isWauhRemovalActive() && !barrierListener.isBlockRemovalActive() && !isAllRemovalActive() && !waterBucketListener.isTsunamiActive() || explosionTrigger)) {
            if (event == null && source.equalsIgnoreCase("TNT") || source.equalsIgnoreCase("creeper")) {
                bedrockExplosionSource();
            } else if (event != null) {
                bedrockPlayerSource(event);
            }
        }
    }

    private void bedrockExplosionSource() {
        Player tntPlayer = tntListener.getTntPlayer();
        if (tntPlayer != null) {
            if (!tntPlayer.isOp()) {
                Bukkit.getConsoleSender().sendMessage("Real player not OP");
                return;
            }
        } else {
            Player metaPlayer = Bukkit.getPlayer(tntListener.getTnt().getMetadata(SOURCE).getFirst().asString());
            if (metaPlayer == null) {
                Bukkit.getConsoleSender().sendMessage("Meta player not found");
                return;
            }
            if (!metaPlayer.isOp()) {
                Bukkit.getConsoleSender().sendMessage("Meta player not OP");
                return;
            }
        }
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
        setCurrentRemovingPlayer(null);
        if (tntListener.getTntPlayer() != null || tntListener.getTnt().getMetadata(SOURCE).getFirst().asString() != null) {
            if (tntListener.getTntPlayer() != null) {
                setCurrentRemovingPlayer(tntListener.getTntPlayer());
            } else {
                setCurrentRemovingPlayer(Bukkit.getPlayer(tntListener.getTnt().getMetadata(SOURCE).getFirst().asString()));
            }
        }

        blocksToProcess.add(clickedLocation.getBlock());

        processAllRemoval();
    }

    private void bedrockPlayerSource(BlockBreakEvent event) {
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
        boolean limitReachedThisIteration = false; // Variable to track whether the limit was reached this iteration
        String aR = "All removal: ";
        for (Block block : blocksToProcess) {
            if (processedBlocks.contains(block) || markedBlocks.contains(block)) {
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
            if (showRemoval) {
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
            if (showRemoval) {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), this::processAllRemoval, 2L);
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
            if (showRemoval) {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), this::processAllRemoval, 2L);
            } else {
                processAllRemoval();
            }
        } else {
            displaySummary();
        }
    }

    public void displaySummary() {
        Player player = getCurrentRemovingPlayer();
        // Display the block removal summary to the player
        if (allRemovedCount > 1) {
            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + allRemovedCount + ChatColor.GREEN + " blocks.");
                // Display the block removal summary in the console
                Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GREEN + " removed " + ChatColor.RED + allRemovedCount + ChatColor.GREEN + " blocks.");
            }
            if (!showRemoval) {
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
        Scale scale = setAndGet.getScale();

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
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), this::removeMarkedBlocks, 10L); // Schedule the next batch after 1 tick
        } else if (!removedBlocks.isEmpty()) {
            if (repetitions > 0) {
                repetitions--;
                repeated = true;
                markedBlocks.addAll(removedBlocks);
                removedBlocks.clear();
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), this::removeMarkedBlocks, 100L);
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
                    getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Cleaning up falling blocks (" + repetitions + (repetitions == 1 ? " repetition left)" : " repetitions left)")));
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
                getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Cleaning up blocks, " + markedBlocks.size() + " blocks left. That's " + (markedBlocks.size() / scaledBlocksPerIteration + 1) + (markedBlocks.size() / scaledBlocksPerIteration == 1 ? " iteration" : " iterations") + " left"));
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