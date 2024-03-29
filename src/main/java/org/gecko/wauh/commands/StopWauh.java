package org.gecko.wauh.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gecko.wauh.listeners.BarrierListener;
import org.gecko.wauh.listeners.BedrockListener;
import org.gecko.wauh.listeners.BucketListener;
import org.gecko.wauh.listeners.WaterBucketListener;

public class StopWauh implements CommandExecutor {
    public static final String STOPPED = "stopped.";
    private final BucketListener bucketListener;
    private final BarrierListener barrierListener;
    private final BedrockListener bedrockListener;
    private final WaterBucketListener waterBucketListener;

    public StopWauh(BucketListener bucketListener, BarrierListener barrierListener, BedrockListener bedrockListener, WaterBucketListener waterBucketListener) {
        this.bucketListener = bucketListener;
        this.barrierListener = barrierListener;
        this.bedrockListener = bedrockListener;
        this.waterBucketListener = waterBucketListener;
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
        if (!bucketListener.isWauhRemovalActive() && !barrierListener.isBlockRemovalActive() && !bedrockListener.isAllRemovalActive() && !waterBucketListener.isTsunamiActive()) {
            player.sendMessage(ChatColor.RED + "There are no operations running");
        }
        return true;
    }
}