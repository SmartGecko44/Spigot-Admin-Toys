package org.gecko.spigotadmintoys.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gecko.spigotadmintoys.logic.SetAndGet;

public class ToggleRemovalView implements CommandExecutor {
    private final SetAndGet setAndGet;

    public ToggleRemovalView(SetAndGet setAndGet) {
        this.setAndGet = setAndGet;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            setAndGet.toggleRemovalView();
            player.sendMessage("Removal visibility set to " + (!setAndGet.getShowRemoval() ? ChatColor.RED + "false" : ChatColor.GREEN + "true"));
        }
        return true;
    }
}