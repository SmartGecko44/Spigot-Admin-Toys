package org.gecko.wauh.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gecko.wauh.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ench implements CommandExecutor {

    private final Logger logger = Logger.getLogger(Main.class.getName());

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 2) {
                String operation = args[0].toLowerCase();

                    ItemStack enchItem = ((Player) sender).getInventory().getItemInMainHand();
                    if (Main.getPlugin(Main.class).getEnchantmentHandler().getCanEnchant(operation, ((Player) sender).getInventory().getItemInMainHand())) {
                        try {
                            String level = args[1];
                            int maxLevel = Main.getPlugin(Main.class).getEnchantmentHandler().getMaxLevelEnch(operation.toLowerCase());
                            int minLevel = Main.getPlugin(Main.class).getEnchantmentHandler().getMinLevelEnch(operation.toLowerCase());
                            if (Integer.parseInt(level) <= maxLevel && Integer.parseInt(level) > minLevel) {
                                if ((minLevel & maxLevel) == -1) {
                                    sender.sendMessage("Enchantment not found.");
                                    return true;
                                }
                                // Add or update the lore to include enchantment information
                                level = convertToRomanNumerals(Integer.parseInt(level));
                                updateItemLore(enchItem, operation.toUpperCase(), level);
                                sender.sendMessage("Success! Your item now has " + operation.toUpperCase() + " " + level);
                            } else {
                                sender.sendMessage(ChatColor.RED + "Level too high, max is " + maxLevel);
                            }

                        } catch (NumberFormatException e) {
                            sender.sendMessage("Please specify a valid integer.");
                            logger.log(Level.SEVERE, "Error:" + e);
                        }
                    }
            } else {
                sender.sendMessage("Usage: /ench [enchantment] <level>");
                return true;
            }
        } else {
            sender.sendMessage("Only players can execute this command");
        }
        return true;
    }

    private void updateItemLore(ItemStack item, String enchantmentName, String enchantmentLevel) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        // Clear existing lore related to the enchantment

        if (Integer.parseInt(enchantmentLevel) == 0) {
            lore.removeIf(line -> line.startsWith(ChatColor.GRAY + enchantmentName));
        } else {
            lore.removeIf(line -> line.startsWith(ChatColor.GRAY + enchantmentName));
            lore.add(ChatColor.GRAY + enchantmentName + " " + enchantmentLevel);
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

        return String.valueOf(result);
    }

}
