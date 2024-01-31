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
import org.gecko.wauh.Main;
import org.gecko.wauh.enchantments.logic.EnchantmentHandler;

import java.util.ArrayList;
import java.util.List;

public class Ench implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public Ench(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player senderPlayer)) {
            sender.sendMessage("Only players can execute this command");
            return true;
        }

        if (args.length != 2) {
            return false;
        }

        String operation = args[0].toLowerCase();
        ItemStack enchItem = senderPlayer.getInventory().getItemInMainHand();
        String enchantmentNameFinal = operation.substring(0, 1).toUpperCase() + operation.substring(1).toLowerCase();

        if (!plugin.getEnchantmentHandler().getEnchantmentExists(enchantmentNameFinal)) {
            sender.sendMessage("This enchantment does not exist");
            return true;
        }

        if (!plugin.getEnchantmentHandler().getCanEnchant(operation, enchItem)) {
            sender.sendMessage("You cannot enchant this item with this enchantment");
            return true;
        }

        try {
            handleEnchantment(sender, operation, enchItem, enchantmentNameFinal, args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Please specify a valid integer.");
            throw new NumberFormatException("Error: " + e);
        }

        return true;
    }

    private void handleEnchantment(CommandSender sender, String operation, ItemStack enchItem, String enchantmentNameFinal, String levelStr) throws NumberFormatException {
        ArrayList<Enchantment> currentEnchantments = new ArrayList<>(enchItem.getEnchantments().keySet());
        int level = Integer.parseInt(levelStr);
        int maxLevel = plugin.getEnchantmentHandler().getMaxLevelEnch(operation.toLowerCase());
        int minLevel = plugin.getEnchantmentHandler().getMinLevelEnch(operation.toLowerCase());

        if (level > maxLevel || level < minLevel) {
            sender.sendMessage(ChatColor.RED + "Level too high, max is " + maxLevel);
            return;
        }

        if (level == 0) {
            removeEnchantment(sender, enchItem, enchantmentNameFinal, operation);
            return;
        }

        List<Enchantment> conflictingEnchants = plugin.getEnchantmentHandler().getConflicting(operation.toLowerCase(), currentEnchantments);
        if (conflictingEnchants != null && !conflictingEnchants.isEmpty()) {
            handleConflictingEnchantments(sender, conflictingEnchants);
            return;
        }

        addEnchantment(sender, enchItem, enchantmentNameFinal, operation, level);
    }

    private void handleConflictingEnchantments(CommandSender sender, List<Enchantment> conflictingEnchants) {
        if (conflictingEnchants.size() == 1) {
            sender.sendMessage("This enchantment conflicts with another enchantment on this item");
            sender.sendMessage("Conflicting enchantment: " + conflictingEnchants);
        } else {
            sender.sendMessage("This enchantment conflicts with multiple enchantments on this item");
            sender.sendMessage("Conflicting enchantments: " + conflictingEnchants);
        }
    }

    private void addEnchantment(CommandSender sender, ItemStack enchItem, String enchantmentNameFinal, String operation, int level) {
        String levelRoman = convertToRomanNumerals(level);
        if (level <= 10) {
            enchItem.addEnchantment(Enchantment.getByName(enchantmentNameFinal), level);
        } else {
            enchItem.addUnsafeEnchantment(Enchantment.getByName(enchantmentNameFinal), level);
        }
        updateItemLore(enchItem, operation, levelRoman, level);
        sender.sendMessage("Success! Your item now has " + enchantmentNameFinal + " " + levelRoman);
    }

    private void removeEnchantment(CommandSender sender, ItemStack enchItem, String enchantmentNameFinal, String operation) {
        enchItem.removeEnchantment(Enchantment.getByName(enchantmentNameFinal));
        updateItemLore(enchItem, operation, null, 0);
        sender.sendMessage("Success! " + enchantmentNameFinal + " has been removed from your item");
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
                    completions.add(enchantment.getName().toLowerCase());
                }
            }
        }
        return completions;
    }
}
