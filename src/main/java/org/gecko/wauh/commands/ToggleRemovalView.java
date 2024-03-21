package org.gecko.wauh.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gecko.wauh.logic.SetAndGet;

public class ToggleRemovalView implements CommandExecutor {
    private final SetAndGet setAndGet;

    public ToggleRemovalView(SetAndGet setAndGet) {
        this.setAndGet = setAndGet;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            boolean showRemoval = setAndGet.getShowRemoval();
            setAndGet.setRemovalView(!showRemoval);
            if (!setAndGet.getShowRemoval()) {
                player.sendMessage("Removal visibility set to " + ChatColor.RED + "false");
            } else {
                player.sendMessage("Removal visibility set to " + ChatColor.GREEN + "true");
            }
        }
        return true;
    }
}