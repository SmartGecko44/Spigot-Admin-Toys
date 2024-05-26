package org.gecko.spigotadmintoys.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gecko.spigotadmintoys.listeners.*;
import org.gecko.spigotadmintoys.logic.SetAndGet;

public class StopWauh implements CommandExecutor {
    public static final String STOPPED = "stopped.";
    private final BucketListener bucketListener;
    private final BarrierListener barrierListener;
    private final BedrockListener bedrockListener;
    private final WaterBucketListener waterBucketListener;
    private final SphereMaker sphereMaker;

    public StopWauh(SetAndGet setAndGet) {
        this.bucketListener = setAndGet.getBucketListener();
        this.barrierListener = setAndGet.getBarrierListener();
        this.bedrockListener = setAndGet.getBedrockListener();
        this.waterBucketListener = setAndGet.getWaterBucketListener();
        this.sphereMaker = setAndGet.getBlocker();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length != 0) {
            return false;
        }

        if (bucketListener.isWauhRemovalActive()) {
            bucketListener.setStopWaterRemoval(true);
            player.sendMessage(ChatColor.GREEN + "Wauh removal " + ChatColor.RED + ChatColor.BOLD + STOPPED);
        }
        if (barrierListener.isBlockRemovalActive()) {
            barrierListener.setStopBlockRemoval(true);
            player.sendMessage(ChatColor.GREEN + "Block removal " + ChatColor.RED + ChatColor.BOLD + STOPPED);
        }
        if (bedrockListener.isAllRemovalActive()) {
            bedrockListener.setStopAllRemoval(true);
            player.sendMessage(ChatColor.GREEN + "All removal " + ChatColor.RED + ChatColor.BOLD + STOPPED);
        }
        if (waterBucketListener.isTsunamiActive()) {
            waterBucketListener.setStopTsunami(true);
            player.sendMessage(ChatColor.GREEN + "Tsunami " + ChatColor.RED + ChatColor.BOLD + STOPPED);
        }
        if (sphereMaker.isSphereingActive()) {
            sphereMaker.setStopSphereing(true);
            player.sendMessage(ChatColor.GREEN + "Sphereing " + ChatColor.RED + ChatColor.BOLD + STOPPED);
        }
        if (!bucketListener.isWauhRemovalActive() && !barrierListener.isBlockRemovalActive() && !bedrockListener.isAllRemovalActive() && !waterBucketListener.isTsunamiActive()) {
            player.sendMessage(ChatColor.RED + "There are no operations running");
        }
        return true;
    }
}