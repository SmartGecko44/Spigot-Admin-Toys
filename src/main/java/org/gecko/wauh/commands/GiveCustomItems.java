package org.gecko.wauh.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.gecko.wauh.items.TriggerItems;

import java.util.List;

public class GiveCustomItems implements CommandExecutor, TabCompleter {

    private TriggerItems items;
    private ItemStack createdItem;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        createdItem = null;
        items = new TriggerItems();
        if (sender instanceof Player) {
            if (args.length == 1) {
                String operation = args[0].toLowerCase();

                if (operation.equals("bucket") || operation.equals("barrier") || operation.equals("bedrock") || operation.equals("tsunami")) {
                    if (operation.equals("bucket")) {
                         createdItem = items.createCustomItem(Material.BUCKET, "Water Drainer", (short) 0, "Removes all fluids", "Custom Bucket");
                         ((Player) sender).getInventory().addItem(createdItem);
                    } else if (operation.equals("barrier")) {
                        createdItem = items.createCustomItem(Material.BARRIER, "Surface Remover", (short) 0, "Removes grass and dirt blocks", "Custom Barrier");
                        ((Player) sender).getInventory().addItem(createdItem);
                    } else if (operation.equals("bedrock")) {
                        createdItem = items.createCustomItem(Material.BEDROCK, "Water Drainer", (short) 0, "Removes almost all blocks", "Custom Bedrock");
                        ((Player) sender).getInventory().addItem(createdItem);
                    } else {
                        createdItem = items.createCustomItem(Material.WATER_BUCKET, "Water Drainer", (short) 0, "Creates a tsunami if you shift + right click on a block", "Custom Tsunami");
                        ((Player) sender).getInventory().addItem(createdItem);
                    }
                } else return true;
            } else {
                sender.sendMessage("Usage: /givecustomitems [bucket/barrier/bedrock/tsunami]");
                return true;
            }
        } else {
            sender.sendMessage("Only players can execute this command");
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
