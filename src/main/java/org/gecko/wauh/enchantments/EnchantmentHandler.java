package org.gecko.wauh.enchantments;

import org.bukkit.inventory.ItemStack;
import org.gecko.wauh.enchantments.weapons.swords.Disarm;

public class EnchantmentHandler {

    public int getMaxLevelEnch(String name) {
        Disarm disarm = new Disarm(100);
        if (name.equalsIgnoreCase("disarm")) {
            return disarm.getMaxLevel();
        } else {
            return -1;
        }
    }

    public int getMinLevelEnch(String name) {
        Disarm disarm = new Disarm(100);
        if (name.equalsIgnoreCase("disarm")) {
            return disarm.getStartLevel();
        } else {
            return -1;
        }
    }

    public boolean getCanEnchant(String name, ItemStack item) {
        Disarm disarm = new Disarm(100);
        if (name.equalsIgnoreCase("disarm")) {
            return disarm.canEnchantItem(item);
        } else {
            return false;
        }
    }
}
