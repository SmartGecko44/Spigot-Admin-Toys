package org.gecko.spigotadmintoys.enchantments.logic;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.gecko.spigotadmintoys.enchantments.enchants.weapons.bows.*;
import org.gecko.spigotadmintoys.enchantments.enchants.weapons.swords.Disarm;
import org.gecko.spigotadmintoys.enchantments.tools.pickaxes.Drill;
import org.gecko.spigotadmintoys.enchantments.tools.pickaxes.Smelt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnchantmentHandler {

    private static final List<Enchantment> enchantments = new ArrayList<>();

    public EnchantmentHandler() {
        enchantments.add(new Disarm());
        enchantments.add(new Aim());
        enchantments.add(new Multishot());
        enchantments.add(new Drill());
        enchantments.add(new Smelt());
        enchantments.add(new Glow());
        enchantments.add(new Endanger());
        enchantments.add(new Explosive());
    }

    public static List<Enchantment> getAllEnchantments() {
        return enchantments;
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
            return Collections.emptyList();
        }

        for (Enchantment conflictingEnchant : enchantments) {
            if (enchantment.conflictsWith(conflictingEnchant)) {
                conflictingEnchantments.add(conflictingEnchant);
            }
        }

        if (conflictingEnchantments.isEmpty()) {
            return Collections.emptyList();
        }

        return conflictingEnchantments;
    }

}
