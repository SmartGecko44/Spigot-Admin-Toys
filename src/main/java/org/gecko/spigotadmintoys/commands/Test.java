package org.gecko.spigotadmintoys.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gecko.spigotadmintoys.gui.ConfigGUI;

public class Test implements CommandExecutor {
    final ConfigGUI configGUI;

    public Test(ConfigGUI configGUI) {
        this.configGUI = configGUI;
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

        if (!configGUI.getGui().getTitle().equals("Test (WIP)")) {
            configGUI.generateGUI();
        }
        configGUI.openGUI(player);
        return true;
    }
}