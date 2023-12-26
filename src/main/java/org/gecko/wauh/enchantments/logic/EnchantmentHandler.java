package org.gecko.wauh.enchantments.logic;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.gecko.wauh.enchantments.enchants.weapons.bows.Aim;
import org.gecko.wauh.enchantments.enchants.weapons.bows.Multishot;
import org.gecko.wauh.enchantments.enchants.weapons.swords.Disarm;
import org.gecko.wauh.enchantments.tools.pickaxes.Drill;
import org.gecko.wauh.enchantments.tools.pickaxes.Smelt;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentHandler {

    private static List<Enchantment> enchantments;

    public EnchantmentHandler() {
        enchantments = new ArrayList<>();
        enchantments.add(new Disarm());
        enchantments.add(new Aim());
        enchantments.add(new Multishot());
        enchantments.add(new Drill());
        enchantments.add(new Smelt());
    }

    public boolean getEnchantmentExists(String name) {
        return getEnchantmentByName(name) != null;
    }

    public int getMaxLevelEnch(String name) {
        Enchantment enchantment = getEnchantmentByName(name);
        return enchantment != null ? enchantment.getMaxLevel() : -1;
    }

    public int getMinLevelEnch(String name) {
        Enchantment enchantment = getEnchantmentByName(name);
        return enchantment != null ? enchantment.getStartLevel() : -1;
    }

    public boolean getCanEnchant(String enchantmentName, ItemStack item) {
        Enchantment enchantment = getEnchantmentByName(enchantmentName);
        return enchantment != null && enchantment.canEnchantItem(item);
    }

    private Enchantment getEnchantmentByName(String enchantmentName) {
        for (Enchantment enchantment : enchantments) {
            if (enchantment.getName().equalsIgnoreCase(enchantmentName)) {
                return enchantment;
            }
        }
        return null;
    }

    public List<Enchantment> getConflicting(String enchantmentName, List<Enchantment> enchantments) {
        Enchantment enchantment = getEnchantmentByName(enchantmentName);
        List<Enchantment> conflictingEnchantments = new ArrayList<>();

        if (enchantment == null) {
            return null;
        }

        for (Enchantment conflictingEnchant : enchantments) {
            if (enchantment.conflictsWith(conflictingEnchant)) {
                conflictingEnchantments.add(conflictingEnchant);
            }
        }

        if (conflictingEnchantments.isEmpty()) {
            return null;
        }

        return conflictingEnchantments;
    }

    public static List<Enchantment> getAllEnchantments() {
        return enchantments;
    }

}
