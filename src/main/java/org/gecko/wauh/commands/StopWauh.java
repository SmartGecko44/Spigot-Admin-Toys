package org.gecko.wauh.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gecko.wauh.barriuh.BarrierListener;
import org.gecko.wauh.wauhbuck.BucketListener;

public class StopWauh implements CommandExecutor {
    private final BucketListener bucketListener;
    private final BarrierListener barrierListener;

    public StopWauh(BucketListener bucketListener, BarrierListener barrierListener) {
        this.bucketListener = bucketListener;
        this.barrierListener = barrierListener;
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
        if (!bucketListener.wauhRemovalActive && !barrierListener.blockRemovalActive) {
            player.sendMessage(ChatColor.RED + "There are no block removals running");
        }

        return true;
    }
}