package org.gecko.wauh.enchantments.enchants.weapons.bows;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

public class Aim extends Enchantment implements Listener {

    public Aim(int id) {
        super(id);
    }

    @Override
    public String getName() {
        return "Aim";
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

                if (bow.containsEnchantment(this)) {
                    int level = bow.getEnchantmentLevel(this);
                    int range = level + 1; // Increase range by 1 block for each level

                    scheduleTargetUpdate(arrow, shooter, range);
                }
            }
        }
    }

    private void scheduleTargetUpdate(Arrow arrow, Player shooter, int range) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(
                Bukkit.getPluginManager().getPlugin("Wauh"),
                () -> updateArrowTarget(arrow, shooter, range),
                0L,
                2L  // Update every second (adjust as needed)
        );
    }

    private void updateArrowTarget(Arrow arrow, Player shooter, int range) {
        if (!arrow.isValid()) {
            return; // Arrow is no longer valid (e.g., it hit something)
        }

        List<Entity> nearbyEntities = arrow.getNearbyEntities(range, range, range);
        Entity target = findNearestVisibleEntity(nearbyEntities, shooter, arrow);

        if (target != null) {
            // Adjust target location to the center of the entity
            Vector targetLocation = target.getLocation().toVector().add(new Vector(0, (target.getHeight() - 0.2), 0));

            Vector direction = targetLocation.subtract(arrow.getLocation().toVector());
            arrow.setVelocity(direction.normalize().multiply(arrow.getVelocity().length()));
        }
    }

    private Entity findNearestVisibleEntity(List<Entity> entities, Player shooter, Arrow arrow) {
        double minDistance = Double.MAX_VALUE;
        Entity nearestEntity = null;

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity && !entity.equals(shooter) && entity.isValid()) {
                LivingEntity livingEntity = (LivingEntity) entity;

                // Check line of sight
                if (livingEntity.hasLineOfSight(arrow)) {
                    // Use the arrow's location for distance calculation
                    double distance = arrow.getLocation().distanceSquared(entity.getLocation());
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestEntity = entity;
                    }
                }
            }
        }

        return nearestEntity;
    }
}
