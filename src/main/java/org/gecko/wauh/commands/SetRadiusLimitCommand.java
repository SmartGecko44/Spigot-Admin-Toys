package org.gecko.wauh.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gecko.wauh.Main;

public class SetRadiusLimitCommand implements CommandExecutor {
    private final Main plugin;  // Reference to the Main class

    public SetRadiusLimitCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 1) {
                // Check if the argument is an integer
                try {
                    int newLimit = Integer.parseInt(args[0]);
                    plugin.setRadiusLimit(newLimit);  // Use the setter method
                    player.sendMessage("Radius limit set to " + newLimit);
                } catch (NumberFormatException e) {
                    player.sendMessage("Please specify a valid integer.");
                }
            } else {
                player.sendMessage("Usage: /setradiuslimit <integer>");
            }
        } else {
            sender.sendMessage("Only players can use this command.");
        }
        return true;
    }
}
