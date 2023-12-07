package org.gecko.wauh;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.wauh.commands.test;
import org.gecko.wauh.data.ConfigurationManager;
import org.gecko.wauh.gui.ConfigGUI;
import org.gecko.wauh.listeners.*;
import org.gecko.wauh.commands.SetRadiusLimitCommand;
import org.gecko.wauh.commands.StopWauh;
import org.gecko.wauh.commands.ToggleRemovalView;

public final class Main extends JavaPlugin {

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
    private ConfigGUI configGUI;
    ConfigurationManager configManager;
    FileConfiguration config;

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
        configManager = new ConfigurationManager(Main.getPlugin(Main.class));
        config = configManager.getConfig();
        configGUI = new ConfigGUI(this);

        // Register the listeners
        getServer().getPluginManager().registerEvents(bucketListener, this);
        getServer().getPluginManager().registerEvents(barrierListener, this);
        getServer().getPluginManager().registerEvents(bedrockListener, this);
        getServer().getPluginManager().registerEvents(waterBucketListener, this);
        getServer().getPluginManager().registerEvents(tntListener, this);
        getServer().getPluginManager().registerEvents(creeperListener, this);
        getServer().getPluginManager().registerEvents(configGUI, this);

        // Register the StopWauh command with the listeners as arguments
        this.getCommand("stopwauh").setExecutor(new StopWauh(bucketListener, barrierListener, bedrockListener, waterBucketListener));
        this.getCommand("setradiuslimit").setExecutor(new SetRadiusLimitCommand(this));
        this.getCommand("setradiuslimit").setTabCompleter(new SetRadiusLimitCommand(this));
        this.getCommand("toggleremovalview").setExecutor(new ToggleRemovalView(this));
        this.getCommand("test").setExecutor(new test(configGUI));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        configManager.saveConfig();
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
    public ConfigurationManager getConfigManager() {
        return configManager;
    }
    public ConfigGUI getConfigGUI() {
        return configGUI;
    }
}
