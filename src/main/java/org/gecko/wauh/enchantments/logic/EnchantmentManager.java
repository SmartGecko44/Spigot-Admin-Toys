package org.gecko.wauh.enchantments.logic;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EnchantmentManager {

    private final List<Enchantment> enchantments;

    public EnchantmentManager(String enchantmentsPackage) {
        this.enchantments = new ArrayList<>();
        // Automatically detect and initialize enchantments
        initEnchantments(enchantmentsPackage);
    }

    private void initEnchantments(String enchantmentsPackage) {
        Reflections reflections = new Reflections(enchantmentsPackage);
        Set<Class<? extends Enchantment>> enchantmentClasses = reflections.getSubTypesOf(Enchantment.class);

        for (Class<? extends Enchantment> enchantmentClass : enchantmentClasses) {
            try {
                Enchantment enchantment = enchantmentClass.getDeclaredConstructor(int.class).newInstance(100);
                enchantments.add(enchantment);
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
                // Handle the exception as needed
            }
        }
    }

    public void registerEnchantments(Plugin plugin) {
        for (Enchantment enchantment : enchantments) {
            plugin.getServer().getPluginManager().registerEvents(new EnchantmentListener(enchantment), plugin);
            // You might also want to register other things related to the enchantment, such as commands or tasks
        }
    }
}
