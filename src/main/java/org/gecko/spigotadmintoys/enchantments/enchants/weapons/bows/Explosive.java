package org.gecko.spigotadmintoys.enchantments.enchants.weapons.bows;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.inventory.ItemStack;

public class Explosive extends Enchantment {

    public static final String EXPLOSIVESTRING = "Explosive";

    public Explosive() {
        super(107);
    }

    @Override
    public String getName() {
        return EXPLOSIVESTRING;
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.BOW;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return itemStack.getType().equals(Material.BOW);
    }

    public void onProjectileHit(ItemStack bow, Arrow arrow) {
        if (bow.getEnchantments().containsKey(Enchantment.getByName(EXPLOSIVESTRING))) {
            int level = bow.getEnchantments().get(Enchantment.getByName(EXPLOSIVESTRING));
            arrow.getWorld().createExplosion(arrow.getLocation(), level);
            if (arrow.isValid()) {
                arrow.remove();
            }
        }
    }

}
