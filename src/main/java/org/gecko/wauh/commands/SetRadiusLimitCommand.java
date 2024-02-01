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
    public static final String PLAYER = "player";
    public static final String CREEPER = "creeper";
    private final Main plugin;  // Reference to the Main class

    public SetRadiusLimitCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length != 2) {
            return false;
        }

        String operation = args[0].toLowerCase();
        int newLimit;

        try {
            newLimit = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Please specify a valid integer.");
            return true;
        }

        if (newLimit < 2) {
            player.sendMessage(newLimit < 0 ? "The limit must be a positive value." : "Please specify a limit above 1");
            return true;
        }

        switch (operation) {
            case "tnt":
                plugin.setTntRadiusLimit(newLimit);
                player.sendMessage("TNT radius set to " + newLimit);
                break;
            case PLAYER:
                plugin.setRadiusLimit(newLimit);
                player.sendMessage("Player operation limit set to " + newLimit);
                break;
            case CREEPER:
                plugin.setCreeperLimit(newLimit);
                player.sendMessage("Creeper radius limit set to " + newLimit);
                break;
            default:
                player.sendMessage("Invalid operation. Use 'tnt', 'player' or 'creeper'.");
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
