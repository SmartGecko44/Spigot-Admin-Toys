package org.gecko.wauh.items.weapons;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.gecko.wauh.enchantments.enchants.weapons.bows.BowListener;
import org.gecko.wauh.items.TriggerItems;

public class Shortbow implements Listener {

    public static final String SHORTBOWCONST = "Shortbow";
    private final TriggerItems triggerItems = new TriggerItems();

    public ItemStack createShortbow() {
        return triggerItems.createCustomItem(Material.BOW, SHORTBOWCONST, (short) 0, "Instantly shoots an arrow", SHORTBOWCONST);
    }

    @EventHandler
    public void onPlayerBowClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.BOW) {
            return;
        }

        NBTItem nbtItem = new NBTItem(event.getPlayer().getInventory().getItemInMainHand());
        String identifier = nbtItem.getString("Ident");

        if (!identifier.equals(SHORTBOWCONST)) {
            return;
        }

        Arrow arrow = event.getPlayer().launchProjectile(Arrow.class);

        arrow.setPickupStatus(Arrow.PickupStatus.CREATIVE_ONLY);
        arrow.setCritical(true);
        if (event.getPlayer().getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.ARROW_FIRE)) {
            arrow.setFireTicks(Integer.MAX_VALUE);
        }

        BowListener bowListener = new BowListener();
        bowListener.projectileLaunch(null, event.getPlayer(), arrow, event.getPlayer().getInventory().getItemInMainHand());

    }

}

