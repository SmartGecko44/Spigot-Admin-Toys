package org.gecko.wauh.enchantments.logic;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;

public class EnchantmentListener implements Listener {

    private final Enchantment enchantment;

    public EnchantmentListener(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    // Implement event handling specific to the enchantment
    // You may use the @EventHandler annotation and handle events accordingly
}
