package org.gecko.wauh.enchantments.enchants.weapons.bows;

import org.bukkit.Bukkit;
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
import org.gecko.wauh.Main;

import java.util.Random;

public class Multishot extends Enchantment implements Listener {
    private boolean multiProgress;

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
        if (!multiProgress) {
            if (event.getEntity() instanceof Arrow) {
                Arrow arrow = (Arrow) event.getEntity();
                if (arrow.getShooter() instanceof Player) {
                    Player shooter = (Player) arrow.getShooter();
                    ItemStack bow = shooter.getInventory().getItemInMainHand();

                    if (bow.containsEnchantment(this)) {
                        int level = bow.getEnchantmentLevel(this);
                        int numArrows = level + 1; // Shoot one more arrow with each level
                        multiProgress = true;
                        for (int i = numArrows; i > 0; i--) {
                            launchAdditionalArrow(arrow, i, numArrows);
                        }
                        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), this::finishMultiArrow, 1);
                    }
                }
            }
        }
    }

    private void finishMultiArrow() {
        multiProgress = false;
    }

    private void launchAdditionalArrow(Arrow originalArrow, int arrowIndex, int totalArrows) {
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
        double angleBetweenArrows = Math.toRadians(10); // Adjust the angle between arrows as needed
        double rotationAngle = arrowIndex * angleBetweenArrows - (angleBetweenArrows * (totalArrows - 1) / 2);
        Vector direction = originalArrow.getVelocity().clone();
        double cos = Math.cos(rotationAngle);
        double sin = Math.sin(rotationAngle);
        direction.setX(cos * direction.getX() - sin * direction.getZ());
        direction.setZ(sin * direction.getX() + cos * direction.getZ());
        additionalArrow.setVelocity(direction.multiply(originalArrow.getVelocity().length()));
    }
}
