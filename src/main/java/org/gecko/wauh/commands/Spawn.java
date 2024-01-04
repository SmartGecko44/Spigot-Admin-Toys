package org.gecko.wauh.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Spawn implements CommandExecutor, TabCompleter {

    // Command to spawn a specified amount of a specified entity
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length >= 2 && args.length <= 3) {
                String entity = args[0].toUpperCase();
                try {
                    int amount = Integer.parseInt(args[1]);
                    int radius = 0; // Default radius if not specified

                    if (args.length == 3) {
                        // If radius is specified, use it
                        radius = Integer.parseInt(args[2]);
                    } else {
                        // Automatically adjust radius to avoid entity cramming limit
                        int maxEntities = 24;
                        radius = Math.max(radius, (int) Math.ceil(Math.sqrt(amount / Math.PI)));
                        radius = Math.max(radius, (int) Math.ceil(Math.sqrt(maxEntities / Math.PI)));
                    }

                    if (amount < 1 || radius < 0) {
                        sender.sendMessage("The amount must be positive, and the radius must be a non-negative value.");
                        return true;
                    } else {
                        EntityType entityType = EntityType.valueOf(entity);

                        if (entityType.isSpawnable()) {
                            double angleIncrement = 2 * Math.PI / amount;
                            for (int i = 0; i < amount; i++) {
                                double angle = i * angleIncrement;
                                double x = radius * Math.cos(angle);
                                double z = radius * Math.sin(angle);
                                player.getWorld().spawnEntity(player.getLocation().add(x, 0, z), entityType);
                            }
                            sender.sendMessage("Spawned " + amount + " " + entity + "s with a radius of " + radius + ".");
                            return true;
                        } else {
                            sender.sendMessage("This entity cannot be spawned.");
                            return true;
                        }
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("Please specify valid integers for amount and radius.");
                    return true;
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("Please specify a valid entity.");
                    return true;
                }
            } else {
                return false;
            }
        } else {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();

            for (EntityType entityType : EntityType.values()) {
                String entityTypeName = entityType.name().toLowerCase();
                if (entityTypeName.startsWith(input) && entityType.isSpawnable()) {
                    completions.add(entityTypeName);
                }
            }
        }
        return completions;
    }
}
