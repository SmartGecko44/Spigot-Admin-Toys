package org.gecko.wauh.enchantments.enchants.weapons.swords;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class Disarm extends Enchantment implements Listener {

    private static final Random r = new Random();

    public Disarm() {
        super(100);
    }

    @Override
    public String getName() {
        return "Disarm";
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
        return EnchantmentTarget.WEAPON;
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
        return itemStack.getType().equals(Material.DIAMOND_SWORD);
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && event.getEntity() instanceof LivingEntity livingEntity) {
            ItemStack weapon = player.getInventory().getItemInMainHand();
            if (weapon.containsEnchantment(this)) {
                int level = weapon.getEnchantmentLevel(this);
                if (r.nextDouble() < 0.1 * level) {
                    removeRandomArmorPiece(livingEntity);
                }
            }
        }
    }

    private void removeRandomArmorPiece(LivingEntity target) {
        EntityEquipment equipment = target.getEquipment();
        if (equipment == null) return;

        ItemStack[] armor = equipment.getArmorContents();
        for (int i = 0; i < armor.length; i++) {
            if (armor[i] != null && !armor[i].getType().equals(Material.AIR)) {
                ItemStack removedItem = armor[i];
                armor[i] = new ItemStack(Material.AIR);
                target.getWorld().dropItem(target.getLocation(), removedItem);
                equipment.setArmorContents(armor);
                break;
            }
        }
    }
}