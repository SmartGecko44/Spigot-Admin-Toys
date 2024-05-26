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
import org.gecko.spigotadmintoys.logic.SetAndGet;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class SphereMaker implements Listener {

    private Set<Block> blocksToProcess = new HashSet<>();
    private final Set<Block> processedBlocks = new HashSet<>();
    private final Set<Block> markedBlocks = new HashSet<>();
    private Location clickedLocation;
    private Player currentRemovingPlayer;
    private int radiusLimit;
    private int highestDist;
    private int realradiusLimit;
    private int removedBlocks;
    private boolean showRemoval;
    private boolean sphereingActive;
    private boolean stopSphereing;
    private final SetAndGet setAndGet;
    private static final Set<Material> IMMUTABLE_MATERIALS = EnumSet.of(Material.BEDROCK, Material.STATIONARY_WATER, Material.WATER, Material.LAVA, Material.STATIONARY_LAVA, Material.TNT, Material.AIR);

    public SphereMaker(SetAndGet setAndGet) {
        this.setAndGet = setAndGet;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!event.getPlayer().isOp() || event.getPlayer().getInventory().getItemInMainHand() == null || event.getPlayer().getInventory().getItemInMainHand().getAmount() == 0 || event.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR) {
            return;
        }

        ConfigurationManager configManager;
        FileConfiguration config;
        configManager = setAndGet.getConfigManager();
        config = configManager.getConfig();
        //TODO: Implement config toggle

        /* if (config.getInt("Blocker enabled") == 0) {
            return;
        } */

        BarrierListener barrierListener = setAndGet.getBarrierListener();
        BedrockListener bedrockListener = setAndGet.getBedrockListener();
        BucketListener bucketListener = setAndGet.getBucketListener();
        WaterBucketListener waterBucketListener = setAndGet.getWaterBucketListener();
        NBTItem nbtItem = new NBTItem(event.getPlayer().getInventory().getItemInMainHand());
        String identifier = nbtItem.getString("Ident");
        radiusLimit = setAndGet.getRadiusLimit();
        realradiusLimit = setAndGet.getRadiusLimit() - 2;
        if (realradiusLimit > 1 && !barrierListener.isBlockRemovalActive() && !bedrockListener.isAllRemovalActive() && !bucketListener.isWauhRemovalActive() && !waterBucketListener.isTsunamiActive() && !IMMUTABLE_MATERIALS.contains(event.getBlock().getType()) && identifier.equals("SphereMaker")) {
            setSphereingActive(true);
            Player player = event.getPlayer();

            removedBlocks = 0;
            highestDist = 0;
            blocksToProcess.clear();
            processedBlocks.clear();
            setCurrentRemovingPlayer(player);
            clickedLocation = event.getBlock().getLocation();
            showRemoval = setAndGet.getShowRemoval();

            blocksToProcess.add(event.getBlock());
            markedBlocks.add(event.getBlock());

            processBlocking();
        }

    }

    private void processBlocking() {
        if (stopSphereing) {
            stopSphereing = false;
            displaySummary();
            return;
        }

        Set<Block> nextSet = new HashSet<>();
        String blocking = "Bocking: ";
        for (Block block : blocksToProcess) {
            if (processedBlocks.contains(block) || (int) clickedLocation.distance(block.getLocation()) + 1 > radiusLimit - 3) {
                continue;
            }
            int dist = (int) clickedLocation.distance(block.getLocation()) + 1;
            if (dist > highestDist && (dist > 1)) {
                int progressPercentage = (int) ((double) highestDist / (realradiusLimit - 2) * 100);
                highestDist = dist;

                if (getCurrentRemovingPlayer() != null) {
                    if (highestDist < realradiusLimit - 1) {
                        getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + blocking + ChatColor.RED + progressPercentage + "% " + ChatColor.GREEN + "(" + ChatColor.RED + dist + ChatColor.WHITE + "/" + ChatColor.GREEN + realradiusLimit + ")"));
                    } else if (highestDist == realradiusLimit - 1) {
                        getCurrentRemovingPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN + "Radius limit reached. Rounding corners..."));
                    }
                }
            }

            removedBlocks++;

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
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), this::processBlocking, 2L);
            } else {
                processBlocking();
            }
        } else {
            displaySummary();
        }
    }

    private void displaySummary() {
        if (removedBlocks > 1) {
            currentRemovingPlayer.sendMessage("Removed " + removedBlocks + " blocks");

            Bukkit.getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + currentRemovingPlayer.getName() + ChatColor.GREEN + " removed " + ChatColor.RED + removedBlocks + ChatColor.GREEN + " blocks using " + ChatColor.GOLD + "blocker.");
            if (!showRemoval) {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Main.class), this::removeBlockedBlocks, 20L);
            } else {
                clear();
            }
        } else {
            clear();
        }
    }

    private void removeBlockedBlocks() {
        //TODO
    }

    private void clear() {
        blocksToProcess.clear();
        processedBlocks.clear();
        markedBlocks.clear();
        clickedLocation = null;
        radiusLimit = 0;
        highestDist = 0;
        realradiusLimit = 0;
        removedBlocks = 0;
    }

    public boolean isSphereingActive() {
        return sphereingActive;
    }

    public void setSphereingActive(boolean sphereingActive) {
        this.sphereingActive = sphereingActive;
    }

    public Player getCurrentRemovingPlayer() {
        return currentRemovingPlayer;
    }

    public void setCurrentRemovingPlayer(Player currentRemovingPlayer) {
        this.currentRemovingPlayer = currentRemovingPlayer;
    }

    public void setStopSphereing(boolean stopSphereing) {
        this.stopSphereing = stopSphereing;
    }

}
