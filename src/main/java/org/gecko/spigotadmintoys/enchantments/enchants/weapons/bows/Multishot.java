package org.gecko.spigotadmintoys.enchantments.enchants.weapons.bows;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Map;

public class Multishot extends Enchantment {

    public static final String MULTISHOTSTRING = "Multishot";

    public Multishot() {
        super(102);
    }

    @Override
    public String getName() {
        return MULTISHOTSTRING;
    }

    @Override
    public int getMaxLevel() {
        return 300;
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

    public void multishotHandler(Arrow arrow, ItemStack bow) {
        // This uses a map of all enchantments because for some reason, using the preexisting function doesn't work
        Map<Enchantment, Integer> itemEnch = bow.getEnchantments();
        if (itemEnch.containsKey(Enchantment.getByName(MULTISHOTSTRING)) && arrow.isCritical()) {
            int level = itemEnch.get(Enchantment.getByName(MULTISHOTSTRING));

            for (int i = level; i > 0; i--) {
                spawnAdditionalArrow(arrow, i, level);
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
        if (originalArrow.getFireTicks() > 0) {
            additionalArrow.setFireTicks(Integer.MAX_VALUE);
        }
        additionalArrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);

        // Set velocity based on the arrowIndex and totalArrows
        double angleBetweenArrows;
        if (totalArrows < 30) {
            angleBetweenArrows = Math.toRadians(2);
        } else {
            angleBetweenArrows = Math.toRadians((double) 60 / totalArrows); // Adjust the angle between arrows as needed
        }

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
        new Aim().aimHandler((Player) originalArrow.getShooter(), additionalArrow, ((Player) originalArrow.getShooter()).getInventory().getItemInMainHand());
        new Glow().glowCreateHandler(additionalArrow, ((Player) originalArrow.getShooter()).getInventory().getItemInMainHand());
    }

    public void onArrowHitHandler(Arrow arrow, ItemStack bow) {
        if (bow.containsEnchantment(Enchantment.getByName(MULTISHOTSTRING)) && arrow.isCritical()) {
            arrow.remove();
        }
    }
}