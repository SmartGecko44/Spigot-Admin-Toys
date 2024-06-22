package org.gecko.spigotadmintoys;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.spigotadmintoys.enchantments.logic.EnchantmentHandler;
import org.gecko.spigotadmintoys.logic.SetAndGet;
import org.gecko.spigotadmintoys.startup.Register;

import java.lang.reflect.Field;

class RegisterError extends Exception {
    public RegisterError(String errorMessage) {
        super(errorMessage);
    }
}

public final class Main extends JavaPlugin {
    private SetAndGet setAndGet;

    private static void registerEnchantment(Enchantment enchantment) throws RegisterError {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true); // Allow enchantment registration temporarily
            Enchantment.registerEnchantment(enchantment);
        } catch (Exception e) {
            throw new RegisterError("Error while registering enchantment " + enchantment + " Error:" + e);
        } finally {
            try {
                // Set acceptingNew back to false to avoid potential issues
                Field f = Enchantment.class.getDeclaredField("acceptingNew");
                f.setAccessible(true);
                f.set(null, false);
            } catch (Exception ignored) {
                // Ignore any exceptions during cleanup
            }
        }
        // It's been registered!
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + enchantment.getName() + " with ID " + enchantment.hashCode() + " registered");
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Spigot-Admin-Toys has been enabled!");

        // Create instances of some misc classes
        setAndGet = new SetAndGet();

        // Create instances of the listeners
        Register register = new Register();


        // Register the listeners
        register.registerListeners(this, setAndGet);

        // Create enchant instances
        register.registerEnchantmentListeners(this);

        // Register Enchantments
        try {
            for (Enchantment enchantment : EnchantmentHandler.getAllEnchantments()) {
                registerEnchantment(enchantment);
            }
        } catch (IllegalArgumentException | RegisterError ignored) {
            // Ignore any exceptions during enchantment registration
        }

        // Register commands
        register.registerCommands(this, setAndGet);
        // Register TabCompleters
        register.registerTabCompleters(this, setAndGet);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Enchantment.stopAcceptingRegistrations();
    }

    public SetAndGet getSetAndGet() {
        return setAndGet;
    }
}