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

    public static final String BUCKET = "bucket";
    public static final String BARRIER = "barrier";
    public static final String BEDROCK = "bedrock";
    public static final String TSUNAMI = "tsunami";
    private final MirrorItem mirror = new MirrorItem();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        TriggerItems items = new TriggerItems();
        if (sender instanceof Player senderPlayer) {
            if (args.length == 1) {
                String operation = args[0].toLowerCase();

                if (operation.equals(BUCKET) || operation.equals(BARRIER) || operation.equals(BEDROCK) || operation.equals(TSUNAMI) || operation.equals("mirror") || operation.equals("all")) {
                    ItemStack customBucket = items.createCustomItem(Material.BUCKET, "Water Drainer", (short) 0, "Removes all fluids", "Custom Bucket");
                    ItemStack customBarrier = items.createCustomItem(Material.BARRIER, "Surface Remover", (short) 0, "Removes grass and dirt blocks", "Custom Barrier");
                    ItemStack customBedrock = items.createCustomItem(Material.BEDROCK, "Block Obliterator", (short) 0, "Removes almost all blocks", "Custom Bedrock");
                    ItemStack customTsunami = items.createCustomItem(Material.WATER_BUCKET, "Tsunami Bucket", (short) 0, "Creates a tsunami if you shift + right click on a block", "Custom Tsunami");
                    switch (operation) {
                        case BUCKET:
                            senderPlayer.getInventory().addItem(customBucket);
                            break;
                        case BARRIER:
                            ((Player) sender).getInventory().addItem(customBarrier);
                            break;
                        case BEDROCK:
                            ((Player) sender).getInventory().addItem(customBedrock);
                            break;
                        case TSUNAMI:
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
                return false;
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

            if (BUCKET.startsWith(input)) {
                completions.add(BUCKET);
            } else if (BARRIER.startsWith(input)) {
                completions.add(BARRIER);
            } else if (BEDROCK.startsWith(input)) {
                completions.add(BEDROCK);
            } else if (TSUNAMI.startsWith(input)) {
                completions.add(TSUNAMI);
            } else if ("all".startsWith(input)) {
                completions.add("all");
            }
        }
        return completions;
    }
}
