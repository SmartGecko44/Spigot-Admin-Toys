package org.gecko.wauh.enchantments.enchants.weapons.bows;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Multishot extends Enchantment implements Listener {

    public Multishot(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return "Multishot";
    }

    @Override
    public int getMaxLevel() {
        return 10; // You can adjust the maximum level as needed
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

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();
            if (arrow.getShooter() instanceof Player) {
                Player shooter = (Player) arrow.getShooter();
                ItemStack bow = shooter.getInventory().getItemInMainHand();

                if (bow.containsEnchantment(this) && arrow.isCritical()) {
                    int level = bow.getEnchantmentLevel(this);
                    for (int i = level; i > 0; i--) {
                        spawnAdditionalArrow(arrow, i, level);
                    }
                }
            }
        }
    }

    private void spawnAdditionalArrow(Arrow originalArrow, int arrowIndex, int totalArrows) {
        Arrow additionalArrow = originalArrow.getWorld().spawnArrow(
                originalArrow.getLocation(),
                new Vector(0, 0, 0), // Initial velocity is zero
                0.0f,
                0.0f
        );

        // Copy relevant properties from the original arrow
        additionalArrow.setShooter(originalArrow.getShooter());
        additionalArrow.setCritical(originalArrow.isCritical());

        // Set velocity based on the arrowIndex and totalArrows
        double angleBetweenArrows = Math.toRadians(2); // Adjust the angle between arrows as needed
        double rotationAngle = arrowIndex * angleBetweenArrows - (angleBetweenArrows * (totalArrows - 1) / 2);

        // Use the direction of the original arrow as the base direction
        Vector baseDirection = originalArrow.getVelocity().clone().normalize();

        // Rotate the base direction by the calculated angle
        double cos = Math.cos(rotationAngle);
        double sin = Math.sin(rotationAngle);
        double rotatedX = cos * baseDirection.getX() - sin * baseDirection.getZ();
        double rotatedZ = sin * baseDirection.getX() + cos * baseDirection.getZ();

        // Apply the rotated direction to the additional arrow
        additionalArrow.setVelocity(new Vector(rotatedX, baseDirection.getY(), rotatedZ).multiply(originalArrow.getVelocity().length()));
        originalArrow.remove();
    }
}
