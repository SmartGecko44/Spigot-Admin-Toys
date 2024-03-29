package org.gecko.wauh.enchantments.enchants.weapons.bows;

import org.bukkit.entity.Arrow;
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

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Arrow arrow && (arrow.getShooter() instanceof Player shooter)) {
            ItemStack bow = shooter.getInventory().getItemInMainHand();
            new Aim().aimHandler((Player) event.getEntity().getShooter(), arrow, bow);
            new Multishot().multishotHandler(arrow, bow);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow arrow && (arrow.getShooter() instanceof Player shooter)) {
            ItemStack bow = shooter.getInventory().getItemInMainHand();
            new Multishot().onArrowHitHandler(arrow, bow);
            new Aim().onAimHitHandler(bow, event.getHitEntity(), plugin);
        }
    }
}
