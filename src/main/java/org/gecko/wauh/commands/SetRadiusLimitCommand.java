package org.gecko.wauh.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetRadiusLimitCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 1) {
                // Check if the argument is an integer
                try {
                    int value = Integer.parseInt(args[0]);
                    // Perform your logic with the 'value' here
                    player.sendMessage("Value set to " + value);
                } catch (NumberFormatException e) {
                    player.sendMessage("Please specify a valid integer.");
                }
            } else {
                player.sendMessage("Usage: /setvalue <integer>");
            }
        } else {
            sender.sendMessage("Only players can use this command.");
        }
        return true;
    }
}
