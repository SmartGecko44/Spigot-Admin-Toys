package org.gecko.wauh.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.gecko.wauh.items.TriggerItems;
import org.gecko.wauh.items.blocks.MirrorItem;

import java.util.ArrayList;
import java.util.List;

public class GiveCustomItems implements CommandExecutor, TabCompleter {

    private final MirrorItem mirror = new MirrorItem();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        TriggerItems items = new TriggerItems();
        if (sender instanceof Player) {
            if (args.length == 1) {
                String operation = args[0].toLowerCase();

                if (operation.equals("bucket") || operation.equals("barrier") || operation.equals("bedrock") || operation.equals("tsunami") || operation.equals("mirror") || operation.equals("all")) {
                    ItemStack customBucket = items.createCustomItem(Material.BUCKET, "Water Drainer", (short) 0, "Removes all fluids", "Custom Bucket");
                    ItemStack customBarrier = items.createCustomItem(Material.BARRIER, "Surface Remover", (short) 0, "Removes grass and dirt blocks", "Custom Barrier");
                    ItemStack customBedrock = items.createCustomItem(Material.BEDROCK, "Block Obliterator", (short) 0, "Removes almost all blocks", "Custom Bedrock");
                    ItemStack customTsunami = items.createCustomItem(Material.WATER_BUCKET, "Tsunami Bucket", (short) 0, "Creates a tsunami if you shift + right click on a block", "Custom Tsunami");
                    switch (operation) {
                        case "bucket":
                            ((Player) sender).getInventory().addItem(customBucket);
                            break;
                        case "barrier":
                            ((Player) sender).getInventory().addItem(customBarrier);
                            break;
                        case "bedrock":
                            ((Player) sender).getInventory().addItem(customBedrock);
                            break;
                        case "tsunami":
                            ((Player) sender).getInventory().addItem(customTsunami);
                            break;
                        case "mirror":
                            ((Player) sender).getInventory().addItem(mirror.createMirrorItem());
                            break;
                        default:
                            ((Player) sender).getInventory().addItem(customBucket, customBarrier, customBedrock, customTsunami, mirror.createMirrorItem());
                            break;
                    }
                } else return true;
            } else {
                sender.sendMessage("Usage: /givecustomitems [bucket/barrier/bedrock/tsunami/all]");
                return true;
            }
        } else {
            sender.sendMessage("Only players can execute this command");
            return true;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();

            if ("bucket".startsWith(input)) {
                completions.add("bucket");
            } else if ("barrier".startsWith(input)) {
                completions.add("barrier");
            } else if ("bedrock".startsWith(input)) {
                completions.add("bedrock");
            } else if ("tsunami".startsWith(input)) {
                completions.add("tsunami");
            } else if ("all".startsWith(input)) {
                completions.add("all");
            }
        }
        return completions;
    }
}
