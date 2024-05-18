package org.gecko.spigotadmintoys.enchantments.enchants.weapons.bows;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.gecko.spigotadmintoys.Main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Aim extends Enchantment {

    private final Map<Entity, Long> lastArrowHitTimes = new HashMap<>();
    private Main plugin;

    public Aim() {
        super(101);
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

    public void aimHandler(Player shooter, Arrow arrow, ItemStack bow) {
        // This uses a map of all enchantments because for some reason, using the preexisting function doesn't work
        Map<Enchantment, Integer> itemEnch = bow.getEnchantments();
        if (itemEnch.containsKey(Enchantment.getByName("Aim"))) {
            int level = itemEnch.get(Enchantment.getByName("Aim"));
            int range = level + 1; // Increase range by 1 block for each level
            scheduleTargetUpdate(arrow, shooter, range);
        }
    }

    private void scheduleTargetUpdate(Arrow arrow, Player shooter, int range) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(
                Bukkit.getPluginManager().getPlugin("Spigot-Admin-Toys"),
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
            Vector targetLocation;
            // Adjust target location to the center of the entity
            if (target.getHeight() <= 5) {
                targetLocation = target.getLocation().toVector().add(new Vector(0, (target.getHeight() / 1.2), 0));
            } else {
                targetLocation = target.getLocation().toVector().add(new Vector(0, (target.getHeight() / 3), 0));
            }

            Vector direction = targetLocation.subtract(arrow.getLocation().toVector());
            arrow.setVelocity(direction.normalize().multiply(arrow.getVelocity().length()));
        }
    }

    private Entity findNearestVisibleEntity(List<Entity> entities, Player shooter, Arrow arrow) {
        double minDistance = Double.MAX_VALUE;
        Entity nearestEntity = null;

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity livingEntity && !entity.equals(shooter) && entity.isValid() && (livingEntity.hasLineOfSight(arrow))) {
                // Use the arrow's location for distance calculation
                double distance = arrow.getLocation().distanceSquared(entity.getLocation());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestEntity = entity;
                }
            }
        }
        return nearestEntity;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Aim aim = (Aim) obj;
        // No clue what an uninitialized plugin is doing here, but the code breaks if I remove it :(
        return Objects.equals(plugin, aim.plugin) && Objects.equals(lastArrowHitTimes, aim.lastArrowHitTimes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plugin, lastArrowHitTimes);
    }
}
