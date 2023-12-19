package org.gecko.wauh.enchantments.logic;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.gecko.wauh.enchantments.enchants.weapons.bows.Aim;
import org.gecko.wauh.enchantments.enchants.weapons.bows.Multishot;
import org.gecko.wauh.enchantments.enchants.weapons.swords.Disarm;
import org.gecko.wauh.enchantments.tools.Drill;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentHandler {

    private final List<Enchantment> enchantments;

    public EnchantmentHandler() {
        enchantments = new ArrayList<>();
        enchantments.add(new Disarm(100));
        enchantments.add(new Aim(101));
        enchantments.add(new Multishot(102));
        enchantments.add(new Drill(103));
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

}
