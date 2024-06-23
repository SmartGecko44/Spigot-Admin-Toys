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
import org.gecko.spigotadmintoys.logic.Scale;
import org.gecko.spigotadmintoys.logic.SetAndGet;

import java.util.*;

public class SphereMaker implements Listener {

    private Set<Block> blocksToProcess = new HashSet<>();
    private final Set<Block> processedBlocks = new HashSet<>();
    private final Set<Block> markedBlocks = new HashSet<>();
    private final Set<Block> removedBlocks = new HashSet<>();
    private Location clickedLocation;
    private Player currentRemovingPlayer;
    private int radiusLimit;
    private int highestDist;
    private int realradiusLimit;
    private int totalRemovedBlocks;
    private boolean showRemoval;
    private boolean sphereingActive;
    private boolean stopSphereing;
    private final SetAndGet setAndGet;
    private static final Set<Material> IMMUTABLE_MATERIALS = EnumSet.of(Material.BEDROCK, Material.STATIONARY_WATER, Material.WATER, Material.LAVA, Material.STATIONARY_LAVA, Material.TNT, Material.AIR);
    private int repetitions;
    private boolean repeated;

    public SphereMaker(SetAndGet setAndGet) {
        this.setAndGet = setAndGet;
        repetitions = setAndGet.getRepetitions();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getPlayer().isOp() || event.getPlayer().getInventory().getItemInMainHand() == null || event.getPlayer().getInventory().getItemInMainHand().getAmount() == 0 || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
            return;
        }

        FileConfiguration config = setAndGet.getConfigManager().getConfig();

        if (config.getInt("Sphere enabled") == 0) {
            return;
        }

        BarrierListener barrierListener = setAndGet.getBarrierListener();
        BedrockListener bedrockListener = setAndGet.getBedrockListener();
        BucketListener bucketListener = setAndGet.getBucketListener();
        WaterBucketListener waterBucketListener = setAndGet.getWaterBucketListener();
        NBTItem nbtItem = new NBTItem(event.getPlayer().getInventory().getItemInMainHand());
        String identifier = nbtItem.getString("Ident");
        radiusLimit = setAndGet.getRadiusLimit();
        realradiusLimit = setAndGet.getRadiusLimit() - 2;
        if (realradiusLimit > 1 && !barrierListener.isBlockRemovalActive() && !bedrockListener.isAllRemovalActive() && !bucketListener.isWauhRemovalActive() && !waterBucketListener.isTsunamiActive() && !IMMUTABLE_MATERIALS.contains(event.getBlock().getType()) && identifier.equals("SphereMaker")) {
            sphereingActive = true;
            Player player = event.getPlayer();

            totalRemovedBlocks = 0;
            highestDist = 0;
            blocksToProcess.clear();
            processedBlocks.clear();
            currentRemovingPlayer = player;
            clickedLocation = event.getBlock().getLocation();
            showRemoval = setAndGet.getShowRemoval();

            blocksToProcess.add(event.getBlock());
            markedBlocks.add(event.getBlock());

            processSphereing();
        }

    }

    private void processSphereing() {
        if (stopSphereing) {
            stopSphereing = false;
            displaySummary();
            return;
        }

        Set<Block> nextSet = new HashSet<>();
        String sphereing = "Sphereing: ";
        for (Block block : blocksToProcess) {
            if (processedBlocks.contains(block) || (int) clickedLocation.distance(block.getLocation()) + 1 > radiusLimit - 3) {
                continue;
            }
            int dist = (int) clickedLocation.distance(block.getLocation()) + 1;
            if (dist > highestDist && (dist > 1)) {
                int progressPercentage = (int) ((double) highestDist / (realradiusLimit - 2) * 100);
                highestDist = dist;

                if (currentRemovingPlayer != null) {
                    if (highestDist < realradiusLimit + 1) {
                        currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + sphereing + ChatColor.RED + progressPercentage + "% " + ChatColor.GREEN + "(" + ChatColor.RED + (dist + 1) + ChatColor.WHITE + "/" + ChatColor.GREEN + realradiusLimit + ")"));
                    } else if (highestDist == realradiusLimit + 1 && showRemoval) {
                        currentRemovingPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Radius limit reached. Rounding corners..."));
                    }
                }
            }

            totalRemovedBlocks++;

            if (showRemoval) {
                block.setType(Material.AIR);
            } else {
                markedBlocks.add(block);
            }
            setAndGet.getIterateBlocks().iterateBlocks(block, nextSet, IMMUTABLE_MATERIALS, true);
            processedBlocks.add(block);
        }

        blocksToProcess = nextSet;

        if (!blocksToProcess.isEmpty()) {
            if (showRemoval) {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), this::processSphereing, 2L);
            } else {
                processSphereing();
            }
        } else {
            displaySummary();
        }
    }

    private void displaySummary() {
        if (totalRemovedBlocks > 1) {
            currentRemovingPlayer.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.RED + totalRemovedBlocks + ChatColor.GREEN + " blocks.");

            Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + currentRemovingPlayer.getName() + ChatColor.GREEN + " removed " + ChatColor.RED + totalRemovedBlocks + ChatColor.GREEN + " blocks using " + ChatColor.GOLD + "sphereing.");
            if (!showRemoval) {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), this::removeMarkedBlocks, 20L);
            } else {
                clear();
            }
        } else {
            clear();
        }
    }

    private void removeMarkedBlocks() {
        Scale scale = setAndGet.getScale();

        int totalRemovedCount = totalRemovedBlocks;
        if (totalRemovedCount < 50000) {
            for (Block block : markedBlocks) {
                block.setType(Material.AIR);
            }
            clear();
            return;
        } else {
            scale.scaleReverseLogic(totalRemovedCount, radiusLimit, markedBlocks, "sphere", null);
        }
        setAndGet.getBlockRemovalScheduler().scheduleBlockRemoval(markedBlocks, removedBlocks, currentRemovingPlayer, this::removeMarkedBlocks, this::clear, repetitions, this::lowerRepetitionsAndToggleRepeated);
    }

    private void clear() {
        blocksToProcess.clear();
        processedBlocks.clear();
        markedBlocks.clear();
        currentRemovingPlayer = null;
        clickedLocation = null;
        radiusLimit = 0;
        highestDist = 0;
        realradiusLimit = 0;
        totalRemovedBlocks = 0;
        repeated = false;
        repetitions = setAndGet.getRepetitions();
    }

    public boolean isSphereingActive() {
        return sphereingActive;
    }

    public void setStopSphereing(boolean stopSphereing) {
        this.stopSphereing = stopSphereing;
    }

    public Player getCurrentRemovingPlayer() {
        return currentRemovingPlayer;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public boolean isRepeated() {
        return repeated;
    }

    public Set<Block> getRemovedBlocks() {
        return removedBlocks;
    }

    private void lowerRepetitionsAndToggleRepeated() {
        repetitions--;
        repeated = true;
    }
}
