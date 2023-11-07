package org.gecko.wauh.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gecko.wauh.Main;

public class ToggleRemovalView implements CommandExecutor {
    private final Main plugin;
    
    public ToggleRemovalView(Main plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            boolean showRemoval = plugin.getShowRemoval();
            plugin.setRemovalView(!showRemoval);
            if (!plugin.getShowRemoval()) {
                player.sendMessage("Removal visibility set to " + ChatColor.RED + "false");
            } else {
                player.sendMessage("Removal visibility set to " + ChatColor.GREEN + "true");
            }
        }
        return true;
    }
}