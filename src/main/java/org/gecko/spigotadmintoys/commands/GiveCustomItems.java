package org.gecko.spigotadmintoys.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.gecko.spigotadmintoys.items.TriggerItems;
import org.gecko.spigotadmintoys.items.weapons.Shortbow;

import java.util.ArrayList;
import java.util.List;

public class GiveCustomItems implements CommandExecutor, TabCompleter {

    public static final String BUCKET = "bucket";
    public static final String BARRIER = "barrier";
    public static final String BEDROCK = "bedrock";
    public static final String TSUNAMI = "tsunami";
    public static final String SPHERE = "sphere";
    public static final String SHORTBOW = "shortbow";

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        TriggerItems items = new TriggerItems();
        if (args.length != 1) {
            return false;
        }

        if (sender instanceof Player senderPlayer) {
            String operation = args[0].toLowerCase();

            ItemStack customBucket = items.createCustomItem(Material.BUCKET, "Water Drainer", (short) 0, "Removes all fluids", "Custom Bucket");
            ItemStack customBarrier = items.createCustomItem(Material.BARRIER, "Surface Remover", (short) 0, "Removes grass and dirt blocks", "Custom Barrier");
            ItemStack customBedrock = items.createCustomItem(Material.BEDROCK, "Block Obliterator", (short) 0, "Removes almost all blocks", "Custom Bedrock");
            ItemStack customTsunami = items.createCustomItem(Material.WATER_BUCKET, "Tsunami Bucket", (short) 0, "Creates a tsunami if you shift + right click on a block", "Custom Tsunami");
            ItemStack customSphere = items.createCustomItem(Material.FLOWER_POT_ITEM, "Sphere creator", (short) 0, "Creates a sphere with the radius of the radius limit", "SphereMaker");
            final Shortbow shortbowClass = new Shortbow();

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
                case SPHERE:
                    ((Player) sender).getInventory().addItem(customSphere);
                    break;
                case SHORTBOW:
                    ((Player) sender).getInventory().addItem(shortbowClass.createShortbow());
                    break;
                default:
                    sender.sendMessage("Invalid operation");
                    return true;
            }
            sender.sendMessage("Item added to inventory");
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
            }
            if (BARRIER.startsWith(input)) {
                completions.add(BARRIER);
            }
            if (BEDROCK.startsWith(input)) {
                completions.add(BEDROCK);
            }
            if (TSUNAMI.startsWith(input)) {
                completions.add(TSUNAMI);
            }
            if (SPHERE.startsWith(input)) {
                completions.add(SPHERE);
            }
            if (SHORTBOW.startsWith(input)) {
                completions.add(SHORTBOW);
            }
        }
        return completions;
    }
}
