package org.gecko.wauh;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.wauh.blocks.Mirror;
import org.gecko.wauh.commands.*;
import org.gecko.wauh.data.ConfigurationManager;
import org.gecko.wauh.enchantments.enchants.weapons.bows.Aim;
import org.gecko.wauh.enchantments.enchants.weapons.bows.BowListener;
import org.gecko.wauh.enchantments.enchants.weapons.bows.Multishot;
import org.gecko.wauh.enchantments.logic.EnchantmentHandler;
import org.gecko.wauh.enchantments.enchants.weapons.swords.Disarm;
import org.gecko.wauh.enchantments.tools.pickaxes.Drill;
import org.gecko.wauh.enchantments.tools.pickaxes.Smelt;
import org.gecko.wauh.gui.ConfigGUI;
import org.gecko.wauh.listeners.*;

import java.lang.reflect.Field;

public final class Main extends JavaPlugin {

    ConfigurationManager configManager;
    FileConfiguration config;
    private int playerRadiusLimit;
    private int tntRadiusLimit;
    private int creeperRadiusLimit;
    private boolean showRemoval = true;
    private BucketListener bucketListener;
    private BarrierListener barrierListener;
    private BedrockListener bedrockListener;
    private WaterBucketListener waterBucketListener;
    private TNTListener tntListener;
    private CreeperListener creeperListener;
    private final EnchantmentHandler enchantmentHandler = new EnchantmentHandler();

    // Enchantments
    public static final Enchantment disarm = new Disarm();
    public static final Enchantment aim = new Aim();
    public static final Enchantment multishot = new Multishot();
    public static final Enchantment drill = new Drill();
    public static final Enchantment smelt = new Smelt();


    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Yay");

        // Create instances of the listeners
        bucketListener = new BucketListener();
        barrierListener = new BarrierListener();
        bedrockListener = new BedrockListener();
        waterBucketListener = new WaterBucketListener();
        tntListener = new TNTListener();
        creeperListener = new CreeperListener();
        configManager = new ConfigurationManager(this);
        config = configManager.getConfig();
        ConfigGUI configGUI = new ConfigGUI(this);
        Mirror mirror = new Mirror(this);

        // Register the listeners
        getServer().getPluginManager().registerEvents(bucketListener, this);
        getServer().getPluginManager().registerEvents(barrierListener, this);
        getServer().getPluginManager().registerEvents(bedrockListener, this);
        getServer().getPluginManager().registerEvents(waterBucketListener, this);
        getServer().getPluginManager().registerEvents(tntListener, this);
        getServer().getPluginManager().registerEvents(creeperListener, this);
        getServer().getPluginManager().registerEvents(configGUI, this);
        getServer().getPluginManager().registerEvents(mirror, this);

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
        } catch (IllegalArgumentException ignored) {
            // Ignore any exceptions during enchantment registration
        }

        // Register commands
        this.getCommand("stopwauh").setExecutor(new StopWauh(bucketListener, barrierListener, bedrockListener, waterBucketListener));
        this.getCommand("setradiuslimit").setExecutor(new SetRadiusLimitCommand(this));
        this.getCommand("setradiuslimit").setTabCompleter(new SetRadiusLimitCommand(this));
        this.getCommand("toggleremovalview").setExecutor(new ToggleRemovalView(this));
        this.getCommand("test").setExecutor(new test(configGUI));
        this.getCommand("givecustomitems").setExecutor(new GiveCustomItems());
        this.getCommand("givecustomitems").setTabCompleter(new SetRadiusLimitCommand(this));
        this.getCommand("ench").setExecutor(new Ench());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Enchantment.stopAcceptingRegistrations();
    }

    public int getRadiusLimit() {
        playerRadiusLimit = config.getInt("playerRadiusLimit", playerRadiusLimit);
        return playerRadiusLimit + 2;
    }

    public void setRadiusLimit(int newLimit) {
        playerRadiusLimit = newLimit;
        config.set("playerRadiusLimit", playerRadiusLimit);
        configManager.saveConfig();
    }

    public int getTntRadiusLimit() {
        tntRadiusLimit = config.getInt("tntRadiusLimit", tntRadiusLimit);
        return tntRadiusLimit + 2;
    }

    public void setTntRadiusLimit(int newLimit) {
        tntRadiusLimit = newLimit;
        config.set("tntRadiusLimit", tntRadiusLimit);
        configManager.saveConfig();
    }

    public int getCreeperRadiusLimit() {
        creeperRadiusLimit = config.getInt("creeperRadiusLimit", creeperRadiusLimit);
        return creeperRadiusLimit + 2;
    }

    public void setCreeperLimit(int newLimit) {
        creeperRadiusLimit = newLimit;
        config.set("creeperRadiusLimit", creeperRadiusLimit);
        configManager.saveConfig();
    }

    public boolean getShowRemoval() {
        return showRemoval;
    }

    public void setRemovalView(boolean newShowRemoval) {
        showRemoval = newShowRemoval;
    }

    public BucketListener getBucketListener() {
        return bucketListener;
    }

    public BarrierListener getBarrierListener() {
        return barrierListener;
    }

    public BedrockListener getBedrockListener() {
        return bedrockListener;
    }

    public WaterBucketListener getWaterBucketListener() {
        return waterBucketListener;
    }

    public TNTListener getTntListener() {
        return tntListener;
    }

    public CreeperListener getCreeperListener() {
        return creeperListener;
    }

    public EnchantmentHandler getEnchantmentHandler() {
        return enchantmentHandler;
    }

    public static void registerEnchantment(Enchantment enchantment) {
        boolean registered = true;
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true); // Allow enchantment registration temporarily
            Enchantment.registerEnchantment(enchantment);
        } catch (Exception e) {
            registered = false;
            throw new RuntimeException("Error while registering enchantment " + enchantment + " Error:" + e);
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

        if (registered) {
            // It's been registered!
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + enchantment.getName() + " Registered");
        }
    }
}