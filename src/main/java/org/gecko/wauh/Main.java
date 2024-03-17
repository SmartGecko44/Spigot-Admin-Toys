package org.gecko.wauh;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.wauh.commands.*;
import org.gecko.wauh.data.ConfigurationManager;
import org.gecko.wauh.enchantments.enchants.weapons.bows.Aim;
import org.gecko.wauh.enchantments.enchants.weapons.bows.BowListener;
import org.gecko.wauh.enchantments.enchants.weapons.bows.Glow;
import org.gecko.wauh.enchantments.enchants.weapons.bows.Multishot;
import org.gecko.wauh.enchantments.enchants.weapons.swords.Disarm;
import org.gecko.wauh.enchantments.logic.EnchantmentHandler;
import org.gecko.wauh.enchantments.tools.pickaxes.Drill;
import org.gecko.wauh.enchantments.tools.pickaxes.Smelt;
import org.gecko.wauh.gui.ConfigGUI;
import org.gecko.wauh.items.weapons.Shortbow;
import org.gecko.wauh.listeners.*;
import org.gecko.wauh.logic.IterateBlocks;

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
    private final EnchantmentHandler enchantmentHandler = new EnchantmentHandler();
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
    private IterateBlocks iterateBlocks;

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
        Shortbow shortbow;
        // Plugin startup logic
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Yay");

        // Create instances of the listeners
        bucketListener = new BucketListener(this);
        barrierListener = new BarrierListener(this);
        bedrockListener = new BedrockListener(this);
        waterBucketListener = new WaterBucketListener(this);
        tntListener = new TNTListener(this);
        creeperListener = new CreeperListener(this);
        configManager = new ConfigurationManager(this);
        config = configManager.getConfig();
        iterateBlocks = new IterateBlocks();
        ConfigGUI configGUI = new ConfigGUI(this);
        shortbow = new Shortbow(this);


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
        BowListener bowListener = new BowListener(this);
        Drill drillListener = new Drill();
        Smelt smeltListener = new Smelt();
        Glow glowListener = new Glow();

        // Enchantment listeners
        getServer().getPluginManager().registerEvents(disarmListener, this);
        getServer().getPluginManager().registerEvents(bowListener, this);
        getServer().getPluginManager().registerEvents(drillListener, this);
        getServer().getPluginManager().registerEvents(smeltListener, this);
        getServer().getPluginManager().registerEvents(glowListener, this);

        // Register Enchantments
        try {
            registerEnchantment(disarm);
            registerEnchantment(aim);
            registerEnchantment(multishot);
            registerEnchantment(drill);
            registerEnchantment(smelt);
            registerEnchantment(glow);
        } catch (IllegalArgumentException | RegisterError ignored) {
            // Ignore any exceptions during enchantment registration
        }

        // Register commands
        this.getCommand("stopwauh").setExecutor(new StopWauh(bucketListener, barrierListener, bedrockListener, waterBucketListener));
        this.getCommand("setradiuslimit").setExecutor(new SetRadiusLimitCommand(this));
        this.getCommand("toggleremovalview").setExecutor(new ToggleRemovalView(this));
        this.getCommand("Test").setExecutor(new Test(configGUI));
        this.getCommand("givecustomitems").setExecutor(new GiveCustomItems(this));
        this.getCommand("ench").setExecutor(new Ench(this));
        this.getCommand("spawn").setExecutor(new Spawn());
        // Register TabCompleters
        this.getCommand("setradiuslimit").setTabCompleter(new SetRadiusLimitCommand(this));
        this.getCommand("givecustomitems").setTabCompleter(new GiveCustomItems(this));
        this.getCommand("ench").setTabCompleter(new Ench(this));
        this.getCommand("spawn").setTabCompleter(new Spawn());
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

    public IterateBlocks getIterateBlocks() {
        return iterateBlocks;
    }
}