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

public class Disarm extends Enchantment implements Listener {

    public Disarm(int id) {
        super(id);
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
    public void OnPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity) {
            Player player = (Player) event.getDamager();
            ItemStack weapon = player.getInventory().getItemInMainHand();

            if (weapon.containsEnchantment(this)) {
                int level = weapon.getEnchantmentLevel(this);
                double chanceForOne = Math.min(1.0, 0.1 * level);

                if (Math.random() < chanceForOne) {
                    LivingEntity target = (LivingEntity) event.getEntity();
                    removeRandomArmorPiece(target);
                }
            }
        }
    }

    private void removeRandomArmorPiece(LivingEntity target) {
        EntityEquipment equipment = target.getEquipment();

        if (equipment != null) {
            ItemStack[] armor = equipment.getArmorContents();

            // Check if the entity is wearing any armor
            boolean wearingArmor = false;
            for (ItemStack itemStack : armor) {
                if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                    wearingArmor = true;
                    break;
                }
            }

            if (wearingArmor) {
                int maxAttempts = armor.length * 10; // Max attempts to prevent an infinite loop

                while (maxAttempts-- > 0) {
                    int randomSlot = (int) (Math.random() * armor.length);

                    // Check if the armor slot contains an item before removing
                    if (armor[randomSlot] != null && !armor[randomSlot].getType().equals(Material.AIR)) {
                        ItemStack removedItem = armor[randomSlot];
                        armor[randomSlot] = new ItemStack(Material.AIR);
                        target.getWorld().dropItem(target.getLocation(), removedItem);
                        equipment.setArmorContents(armor);
                        break;  // Exit the loop after successfully removing armor
                    }
                }
            }
        }
    }
}
