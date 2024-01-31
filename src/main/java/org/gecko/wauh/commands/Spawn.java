package org.gecko.wauh.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Spawn implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length < 2 || args.length > 3) {
            return false;
        }

        try {
            String entity = args[0].toUpperCase();
            int amount = Integer.parseInt(args[1]);
            int radius = args.length == 3 ? Integer.parseInt(args[2]) : calculateRadius(amount);

            if (amount < 1 || radius < 0) {
                sender.sendMessage("The amount must be positive, and the radius must be a non-negative value.");
                return true;
            }

            EntityType entityType = EntityType.valueOf(entity);

            if (!entityType.isSpawnable()) {
                sender.sendMessage("This entity cannot be spawned.");
                return true;
            }

            spawnEntities(player, amount, radius, entityType);
            entity = entity.toLowerCase();
            sender.sendMessage("Spawned " + amount + " " + entity + "s with a radius of " + radius + ".");
        } catch (NumberFormatException e) {
            sender.sendMessage("Please specify valid integers for amount and radius.");
        } catch (IllegalArgumentException e) {
            sender.sendMessage("Please specify a valid entity.");
        }

        return true;
    }

    private int calculateRadius(int amount) {
        int maxEntitiesPerBlock = 24;
        int blocksNeeded = amount / maxEntitiesPerBlock;
        return (int) Math.ceil(Math.sqrt((blocksNeeded * 2) / Math.PI));
    }

    private void spawnEntities(Player player, int amount, int radius, EntityType entityType) {
        double angleIncrement = 2 * Math.PI / amount;
        for (int i = 0; i < amount; i++) {
            double angle = i * angleIncrement;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            player.getWorld().spawnEntity(player.getLocation().add(x, 0, z), entityType);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            return new ArrayList<>();
        }

        String input = args[0].toLowerCase();
        return Stream.of(EntityType.values())
                .filter(EntityType::isSpawnable)
                .map(EntityType::name)
                .map(String::toLowerCase) // convert to lower case
                .filter(name -> name.startsWith(input))
                .toList();
    }
}