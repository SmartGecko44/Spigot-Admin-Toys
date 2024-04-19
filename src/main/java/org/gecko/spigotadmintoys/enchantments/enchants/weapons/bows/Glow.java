package org.gecko.spigotadmintoys.enchantments.enchants.weapons.bows;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Glow extends Enchantment {

    public static final String GLOWSTRING = "Glow";

    public Glow() {
        super(105);
    }

    @Override
    public String getName() {
        return GLOWSTRING;
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

    public void glowCreateHandler(Arrow arrow, ItemStack bow) {
        if (bow.getEnchantments().containsKey(Enchantment.getByName(GLOWSTRING))) {
            arrow.setGlowing(true);
        }
    }

    public void glowHitHandler(ItemStack bow, LivingEntity entity) {
        if (bow.getEnchantments().containsKey(Enchantment.getByName(GLOWSTRING)) && entity != null) {
                if (entity instanceof Player) {
                    PotionEffect glowEffect = new PotionEffect(PotionEffectType.GLOWING, 100, 1, true, true);
                    entity.addPotionEffect(glowEffect);
                } else {
                    entity.setGlowing(true);
                }

        }
    }
}