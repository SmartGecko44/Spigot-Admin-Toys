package org.gecko.wauh.enchantments.enchants.weapons.bows;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.gecko.wauh.Main;

public class BowListener implements Listener {

    private final Main plugin;

    public BowListener(Main plugin) {
        this.plugin = plugin;
    }

    public void projectileLaunch(ProjectileLaunchEvent event, Player shooterManual, Arrow arrowManual, ItemStack bowManual) {
        if (event != null) {
            if (event.getEntity() instanceof Arrow arrow && (arrow.getShooter() instanceof Player shooter)) {
                ItemStack bow = shooter.getInventory().getItemInMainHand();
                new Aim().aimHandler((Player) event.getEntity().getShooter(), arrow, bow);
                new Multishot().multishotHandler(arrow, bow);
                new Glow().glowCreateHandler(arrow, bow);
            }
        } else {
            new Aim().aimHandler(shooterManual, arrowManual, bowManual);
            new Multishot().multishotHandler(arrowManual, bowManual);
            new Glow().glowCreateHandler(arrowManual, bowManual);
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        projectileLaunch(event, null, null, null);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow arrow && (arrow.getShooter() instanceof Player shooter)) {
            ItemStack bow = shooter.getInventory().getItemInMainHand();
            new Multishot().onArrowHitHandler(arrow, bow);
            new Aim().onAimHitHandler(bow, event.getHitEntity(), plugin);
            new Glow().glowHitHandler(bow, (LivingEntity) event.getHitEntity());
        }
    }
}
