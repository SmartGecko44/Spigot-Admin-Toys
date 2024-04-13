package org.gecko.wauh.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gecko.wauh.enchantments.logic.EnchantmentHandler;
import org.gecko.wauh.logic.SetAndGet;

import java.util.ArrayList;
import java.util.List;

public class Ench implements CommandExecutor, TabCompleter {

    private final SetAndGet setAndGet;

    public Ench(SetAndGet setAndGet) {
        this.setAndGet = setAndGet;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player senderPlayer)) {
            sender.sendMessage("Only players can execute this command");
            return true;
        }

        if (args.length != 2 && args.length != 1) {
            return false;
        }

        String operation = args[0].toLowerCase();
        ItemStack enchItem = senderPlayer.getInventory().getItemInMainHand();
        String enchantmentNameFinal = operation.substring(0, 1).toUpperCase() + operation.substring(1);
        EnchantmentHandler enchantmentHandler = setAndGet.getEnchantmentHandler();

        if (!enchantmentHandler.getEnchantmentExists(enchantmentNameFinal)) {
            sender.sendMessage("This enchantment does not exist");
            return true;
        }

        if (!enchantmentHandler.getCanEnchant(operation, enchItem)) {
            sender.sendMessage("You cannot enchant this item with this enchantment");
            return true;
        }

        try {
            int level = -1;
            if (args.length == 2) {
                level = Integer.parseInt(args[1]);
            }
            int maxLevel = enchantmentHandler.getMaxLevelEnch(operation);
            int minLevel = enchantmentHandler.getMinLevelEnch(operation);

            if ((minLevel & maxLevel) == -1) {
                sender.sendMessage("Enchantment not found.");
                return true;
            }

            List<Enchantment> conflictingEnchants = enchantmentHandler.getConflicting(operation, new ArrayList<>(enchItem.getEnchantments().keySet()));
            if (!conflictingEnchants.isEmpty()) {
                sender.sendMessage("This enchantment conflicts with " + (conflictingEnchants.size() > 1 ? "multiple" : "another") + (conflictingEnchants.size() > 1 ? " enchantments" : " enchantment") + " on this item");
                sender.sendMessage("Conflicting" + (conflictingEnchants.size() > 1 ? " enchantments: " : " enchantment: ") + conflictingEnchants);
                return true;
            }

            if (level == -1 || level == 1) {
                enchItem.addEnchantment(Enchantment.getByName(enchantmentNameFinal), 1);
                updateItemLore(enchItem, operation, "", 1);
                sender.sendMessage("Success! Your item now has " + enchantmentNameFinal + " " + convertToRomanNumerals(1));
                return true;
            }

            if (level == 0) {
                enchItem.removeEnchantment(Enchantment.getByName(enchantmentNameFinal));
                updateItemLore(enchItem, operation, null, level);
                sender.sendMessage("Success! " + enchantmentNameFinal + " has been removed from your item");
                return true;
            }

            if (level < minLevel || level > maxLevel) {
                sender.sendMessage(ChatColor.RED + "Level too " + (level < minLevel ? "low, lowest is " + minLevel : "high, highest is " + maxLevel));
                return true;
            }

            if (level > 1) {
                String levelRoman = convertToRomanNumerals(level);
                if (level <= 10) {
                    enchItem.addEnchantment(Enchantment.getByName(enchantmentNameFinal), level);
                } else {
                    enchItem.addUnsafeEnchantment(Enchantment.getByName(enchantmentNameFinal), level);
                }
                updateItemLore(enchItem, operation, levelRoman, level);
                sender.sendMessage("Success! Your item now has " + enchantmentNameFinal + " " + levelRoman);
            }

        } catch (NumberFormatException e) {
            sender.sendMessage("Please specify a valid integer.");
            throw new NumberFormatException("Error: " + e);
        }
        return true;
    }

    private void updateItemLore(ItemStack item, String enchantmentName, String enchantmentLevel, int level) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        String enchantmentNameFinal = enchantmentName.substring(0, 1).toUpperCase() + enchantmentName.substring(1).toLowerCase();

        // Clear existing lore related to the enchantment
        if (level == 0) {
            lore.removeIf(line -> line.startsWith(ChatColor.GRAY + enchantmentNameFinal));
        } else {
            lore.removeIf(line -> line.startsWith(ChatColor.GRAY + enchantmentNameFinal));
            lore.add(ChatColor.GRAY + enchantmentNameFinal + " " + enchantmentLevel);
        }

        // Add or update the lore to include enchantment information

        meta.setLore(lore);
        item.setItemMeta(meta);
    }


    private String convertToRomanNumerals(int number) {
        if (number < 1 || number > 300) {
            throw new IllegalArgumentException("Number out of range for Roman numerals.");
        }

        String[] romanSymbols = {"I", "IV", "V", "IX", "X", "XL", "L", "XC", "C"};

        int[] arabicValues = {1, 4, 5, 9, 10, 40, 50, 90, 100};

        StringBuilder result = new StringBuilder();

        for (int i = arabicValues.length - 1; i >= 0; i--) {
            while (number >= arabicValues[i]) {
                result.append(romanSymbols[i]);
                number -= arabicValues[i];
            }
        }

        return result.toString();
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();

            for (Enchantment enchantment : EnchantmentHandler.getAllEnchantments()) {
                if (enchantment.getName().toLowerCase().startsWith(input)) {
                    completions.add(enchantment.getName());
                }
            }
        }
        return completions;
    }
}
