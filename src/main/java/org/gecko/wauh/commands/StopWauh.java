package org.gecko.wauh.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gecko.wauh.Listeners.BarrierListener;
import org.gecko.wauh.Listeners.BedrockListener;
import org.gecko.wauh.Listeners.BucketListener;
import org.gecko.wauh.Listeners.WaterBucketListener;

public class StopWauh implements CommandExecutor {
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
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        if (bucketListener.wauhRemovalActive) {
            bucketListener.stopWaterRemoval = true;
            player.sendMessage(ChatColor.GREEN + "Wauh removal " + ChatColor.RED + ChatColor.BOLD + "stopped.");
        }
        if (barrierListener.blockRemovalActive) {
            barrierListener.stopBlockRemoval = true;
            player.sendMessage(ChatColor.GREEN + "Block removal " + ChatColor.RED + ChatColor.BOLD + "stopped.");
        }
        if (bedrockListener.allRemovalActive) {
            bedrockListener.stopAllRemoval = true;
            player.sendMessage(ChatColor.GREEN + "All removal " + ChatColor.RED + ChatColor.BOLD + "stopped.");
        }
        if (waterBucketListener.tsunamiActive) {
            waterBucketListener.stopTsunami = true;
            player.sendMessage(ChatColor.GREEN + "Tsunami " + ChatColor.RED + ChatColor.BOLD + "stopped.");
        }
        if (!bucketListener.wauhRemovalActive && !barrierListener.blockRemovalActive && !bedrockListener.allRemovalActive && !waterBucketListener.tsunamiActive) {
            player.sendMessage(ChatColor.RED + "There are no operations running");
        }

        return true;
    }
}