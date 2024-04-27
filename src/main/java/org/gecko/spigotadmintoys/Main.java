package org.gecko.spigotadmintoys;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.spigotadmintoys.data.ConfigurationManager;
import org.gecko.spigotadmintoys.enchantments.enchants.weapons.bows.*;
import org.gecko.spigotadmintoys.enchantments.enchants.weapons.swords.Disarm;
import org.gecko.spigotadmintoys.enchantments.tools.pickaxes.Drill;
import org.gecko.spigotadmintoys.enchantments.tools.pickaxes.Smelt;
import org.gecko.spigotadmintoys.gui.ConfigGUI;
import org.gecko.spigotadmintoys.listeners.*;
import org.gecko.spigotadmintoys.logic.SetAndGet;
import org.gecko.spigotadmintoys.startup.Register;

import java.lang.reflect.Field;

class RegisterError extends Exception {
    public RegisterError(String errorMessage) {
        super(errorMessage);
    }
}

public final class Main extends JavaPlugin {

    // Enchantments
    public static final Enchantment disarm = new Disarm(); // Id: 100
    public static final Enchantment aim = new Aim(); // Id: 101
    public static final Enchantment multishot = new Multishot(); // Id: 102
    public static final Enchantment drill = new Drill(); // Id: 103
    public static final Enchantment smelt = new Smelt(); // Id: 104
    public static final Enchantment glow = new Glow(); // Id: 105
    public static final Enchantment endanger = new Endanger(); // Id: 106
    public static final Enchantment explosive = new Explosive(); // Id: 107
    ConfigurationManager configManager;
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
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + enchantment.getName() + " Registered");
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Spigot-Admin-Toys has been enabled!");

        configManager = new ConfigurationManager(this);

        TNTListener tntListener = new TNTListener(configManager);
        CreeperListener creeperListener = new CreeperListener(configManager, this);

        // Create instances of some misc classes
        setAndGet = new SetAndGet(configManager, tntListener, creeperListener);

        // Create instances of the listeners
        ConfigGUI configGUI = new ConfigGUI(setAndGet);
        Register register = new Register();


        // Register the listeners
        register.registerListeners(this, setAndGet, configGUI);

        // Create enchant instances
        register.registerEnchantmentListeners(this);

        // Register Enchantments
        try {
            registerEnchantment(disarm);
            registerEnchantment(aim);
            registerEnchantment(multishot);
            registerEnchantment(drill);
            registerEnchantment(smelt);
            registerEnchantment(glow);
            registerEnchantment(endanger);
            registerEnchantment(explosive);
        } catch (IllegalArgumentException | RegisterError ignored) {
            // Ignore any exceptions during enchantment registration
        }

        // Register commands
        register.registerCommands(this, setAndGet, configGUI);
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