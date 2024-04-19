package org.gecko.spigotadmintoys;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.spigotadmintoys.commands.*;
import org.gecko.spigotadmintoys.data.ConfigurationManager;
import org.gecko.spigotadmintoys.enchantments.enchants.weapons.bows.*;
import org.gecko.spigotadmintoys.enchantments.enchants.weapons.swords.Disarm;
import org.gecko.spigotadmintoys.enchantments.tools.pickaxes.Drill;
import org.gecko.spigotadmintoys.enchantments.tools.pickaxes.Smelt;
import org.gecko.spigotadmintoys.gui.ConfigGUI;
import org.gecko.spigotadmintoys.items.weapons.Shortbow;
import org.gecko.spigotadmintoys.listeners.*;
import org.gecko.spigotadmintoys.logic.SetAndGet;

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

    public static void registerEnchantment(Enchantment enchantment) throws RegisterError {
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
        BucketListener bucketListener = setAndGet.getBucketListener();
        BarrierListener barrierListener = setAndGet.getBarrierListener();
        BedrockListener bedrockListener = setAndGet.getBedrockListener();
        WaterBucketListener waterBucketListener = setAndGet.getWaterBucketListener();
        ConfigGUI configGUI = new ConfigGUI(setAndGet);
        Shortbow shortbow = new Shortbow();


        // Register the listeners
        getServer().getPluginManager().registerEvents(bucketListener, this);
        getServer().getPluginManager().registerEvents(barrierListener, this);
        getServer().getPluginManager().registerEvents(bedrockListener, this);
        getServer().getPluginManager().registerEvents(waterBucketListener, this);
        getServer().getPluginManager().registerEvents(tntListener, this);
        getServer().getPluginManager().registerEvents(creeperListener, this);
        getServer().getPluginManager().registerEvents(configGUI, this);
        getServer().getPluginManager().registerEvents(shortbow, this);

        // Create enchant instances
        Disarm disarmListener = new Disarm();
        BowListener bowListener = new BowListener();
        Drill drillListener = new Drill();
        Smelt smeltListener = new Smelt();

        // Enchantment listeners
        getServer().getPluginManager().registerEvents(disarmListener, this);
        getServer().getPluginManager().registerEvents(bowListener, this);
        getServer().getPluginManager().registerEvents(drillListener, this);
        getServer().getPluginManager().registerEvents(smeltListener, this);

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
        this.getCommand("stopwauh").setExecutor(new StopWauh(bucketListener, barrierListener, bedrockListener, waterBucketListener));
        this.getCommand("setradiuslimit").setExecutor(new SetRadiusLimitCommand(setAndGet));
        this.getCommand("toggleremovalview").setExecutor(new ToggleRemovalView(setAndGet));
        this.getCommand("Test").setExecutor(new Test(configGUI));
        this.getCommand("givecustomitems").setExecutor(new GiveCustomItems());
        this.getCommand("ench").setExecutor(new Ench(setAndGet));
        this.getCommand("spawn").setExecutor(new Spawn());
        // Register TabCompleters
        this.getCommand("setradiuslimit").setTabCompleter(new SetRadiusLimitCommand(setAndGet));
        this.getCommand("givecustomitems").setTabCompleter(new GiveCustomItems());
        this.getCommand("ench").setTabCompleter(new Ench(setAndGet));
        this.getCommand("spawn").setTabCompleter(new Spawn());
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