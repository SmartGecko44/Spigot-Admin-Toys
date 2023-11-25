package org.gecko.wauh.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.gecko.wauh.Main;

import java.util.ArrayList;
import java.util.List;

public class SetRadiusLimitCommand implements CommandExecutor, TabCompleter {
    private final Main plugin;  // Reference to the Main class

    public SetRadiusLimitCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 2) {
                String operation = args[0].toLowerCase(); // Convert to lowercase for case-insensitivity

                if (operation.equals("tnt") || operation.equals("player")) {
                    // Check if the second argument is an integer
                    try {
                        int newLimit = Integer.parseInt(args[1]);

                        if (operation.equals("tnt")) {
                            plugin.setTntRadiusLimit(newLimit);  // Use the setter method for TNT operations
                            player.sendMessage("TNT radius set to " + newLimit);
                        } else {
                            plugin.setRadiusLimit(newLimit); // Use the setter method for player operations
                            player.sendMessage("Player operation limit set to " + newLimit);
                        }
                    } catch (NumberFormatException e) {
                        player.sendMessage("Please specify a valid integer.");
                    }
                } else {
                    player.sendMessage("Invalid operation. Use 'tnt' or 'player'.");
                }
            } else {
                player.sendMessage("Usage: /setradiuslimit [tnt/player] <integer>");
            }
        } else {
            sender.sendMessage("Only players can use this command.");
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();

            // Add completion suggestions based on the input
            if ("tnt".startsWith(input)) {
                completions.add("tnt");
            }
            if ("player".startsWith(input)) {
                completions.add("player");
            }
        }

        return completions;
    }
}
