package org.gecko.spigotadmintoys.enchantments.enchants.weapons.bows;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public class Endanger extends Enchantment {

    private static final String ENDANGERSTRING = "Endanger";

    public Endanger() {
        super(106);
    }

    @Override
    public String getName() {
        return ENDANGERSTRING;
    }

    @Override
    public int getMaxLevel() {
        return 1;
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

    public void onProjectileHit(LivingEntity entity, ItemStack bow) {
        if (bow.getEnchantments().containsKey(Enchantment.getByName(ENDANGERSTRING)) && (entity != null)) {
            entity.setNoDamageTicks(0);

        }
    }
}
