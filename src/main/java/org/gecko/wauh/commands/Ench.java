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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ench implements CommandExecutor, TabCompleter {

    private final Logger logger = Logger.getLogger(Main.class.getName());

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args[0].equalsIgnoreCase("view")) {
            Map<Enchantment, Integer> enches = ((Player) sender).getInventory().getItemInMainHand().getEnchantments();
            sender.sendMessage(enches.toString());
            return true;
        }
        if (sender instanceof Player) {
            if (args.length == 2) {
                String operation = args[0].toLowerCase();

                ItemStack enchItem = ((Player) sender).getInventory().getItemInMainHand();
                String enchantmentNameFinal = operation.substring(0, 1).toUpperCase() + operation.substring(1).toLowerCase();
                if (Main.getPlugin(Main.class).getEnchantmentHandler().getEnchantmentExists(enchantmentNameFinal)) {
                    if (Main.getPlugin(Main.class).getEnchantmentHandler().getCanEnchant(operation, enchItem)) {
                            try {
                                ArrayList<Enchantment> currentEnchantments = new ArrayList<>(enchItem.getEnchantments().keySet());
                                int level = Integer.parseInt(args[1]);
                                int maxLevel = Main.getPlugin(Main.class).getEnchantmentHandler().getMaxLevelEnch(operation.toLowerCase());
                                int minLevel = Main.getPlugin(Main.class).getEnchantmentHandler().getMinLevelEnch(operation.toLowerCase());
                                if (level <= maxLevel && level >= minLevel || level == 0 || (minLevel == -1 && maxLevel == -1)) {
                                    if ((minLevel & maxLevel) == -1) {
                                        sender.sendMessage("Enchantment not found.");
                                        return true;
                                    }
                                    if (Main.getPlugin(Main.class).getEnchantmentHandler().getConflicting(operation.toLowerCase(), currentEnchantments) != null) {
                                        List<Enchantment> conflictingEnchants = Main.getPlugin(Main.class).getEnchantmentHandler().getConflicting(operation.toLowerCase(), currentEnchantments);
                                        if (conflictingEnchants.size() == 1) {
                                            sender.sendMessage("This enchantment conflicts with another enchantment on this item");
                                            sender.sendMessage("Conflicting enchantment: " + conflictingEnchants);
                                            return true;
                                        } else if (conflictingEnchants.size() > 1) {
                                            sender.sendMessage("This enchantment conflicts with multiple enchantments on this item");
                                            sender.sendMessage("Conflicting enchantments: " + conflictingEnchants);
                                            return true;
                                        }
                                    }
                                    // Add or update the lore to include enchantment information
                                    if (level > 0) {
                                        String levelRoman = convertToRomanNumerals(level);
                                        enchItem.addEnchantment(Enchantment.getByName(enchantmentNameFinal), level);
                                        updateItemLore(enchItem, operation, levelRoman, level);
                                        sender.sendMessage("Success! Your item now has " + enchantmentNameFinal + " " + levelRoman);
                                    } else if (level == 0) {
                                        enchItem.removeEnchantment(Enchantment.getByName(enchantmentNameFinal));
                                        updateItemLore(enchItem, operation, null, level);
                                        sender.sendMessage("Success! " + enchantmentNameFinal + " has been removed from your item");
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.RED + "Level too high, max is " + maxLevel);
                                }

                            } catch (NumberFormatException e) {
                                sender.sendMessage("Please specify a valid integer.");
                                logger.log(Level.SEVERE, "Error:" + e);
                            }
                    } else {
                        sender.sendMessage("You cannot enchant this item with this enchantment");
                        return true;
                    }
                } else {
                    sender.sendMessage("This enchantment does not exist");
                    return true;
                }
            } else {
                sender.sendMessage("Usage: /ench [enchantment] <level>");
                return true;
            }
        } else {
            sender.sendMessage("Only players can execute this command");
            return true;
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
        if (number < 1 || number > 100) {
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
