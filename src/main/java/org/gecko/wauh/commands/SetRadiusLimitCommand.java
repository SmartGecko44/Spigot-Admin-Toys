package org.gecko.wauh.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.gecko.wauh.logic.SetAndGet;

import java.util.ArrayList;
import java.util.List;

public class SetRadiusLimitCommand implements CommandExecutor, TabCompleter {
    public static final String PLAYER = "player";
    public static final String CREEPER = "creeper";
    private final SetAndGet setAndGet;  // Reference to the Main class

    public SetRadiusLimitCommand(SetAndGet setAndGet) {
        this.setAndGet = setAndGet;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {

            if (args.length == 2) {
                String operation = args[0].toLowerCase(); // Convert to lowercase for case-insensitivity

                if (operation.equals("tnt") || operation.equals(PLAYER) || operation.equals(CREEPER)) {
                    // Check if the second argument is an integer
                    try {
                        int newLimit = Integer.parseInt(args[1]);

                        if (newLimit < 0) {
                            player.sendMessage("The limit must be a positive value.");
                            return true;
                        }

                        if (newLimit == 0 || newLimit == 1) {
                            player.sendMessage("Please specify a limit above 1");
                            return true;
                        }

                        if (operation.equals("tnt")) {
                            setAndGet.setTntRadiusLimit(newLimit);  // Use the setter method for TNT operations
                            player.sendMessage("TNT radius set to " + newLimit);
                        } else if (operation.equals(PLAYER)) {
                            setAndGet.setRadiusLimit(newLimit); // Use the setter method for player operations
                            player.sendMessage("Player operation limit set to " + newLimit);
                        } else {
                            setAndGet.setCreeperLimit(newLimit);
                            player.sendMessage("Creeper radius limit set to " + newLimit);
                        }
                    } catch (NumberFormatException e) {
                        player.sendMessage("Please specify a valid integer.");
                        return true;
                    }
                } else {
                    player.sendMessage("Invalid operation. Use 'tnt', 'player' or 'creeper'.");
                    return true;
                }
            } else {
                return false;
            }
        } else {
            sender.sendMessage("Only players can use this command.");
            return true;
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
            if (PLAYER.startsWith(input)) {
                completions.add(PLAYER);
            }
            if (CREEPER.startsWith(input)) {
                completions.add(CREEPER);
            }
        }
        return completions;
    }
}
