package org.gecko.wauh;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.wauh.data.ConfigurationManager;
import org.gecko.wauh.listeners.*;
import org.gecko.wauh.commands.SetRadiusLimitCommand;
import org.gecko.wauh.commands.StopWauh;
import org.gecko.wauh.commands.ToggleRemovalView;
import org.gecko.wauh.logic.ScaleReverse;

public final class Main extends JavaPlugin {

    private int playerRadiusLimit = 20;
    private int tntRadiusLimit = 5;
    private int creeperRadiusLimit = 0;
    private boolean showRemoval = true;
    private BucketListener bucketListener;
    private BarrierListener barrierListener;
    private BedrockListener bedrockListener;
    private WaterBucketListener waterBucketListener;
    private TNTListener tntListener;
    private CreeperListener creeperListener;
    private ScaleReverse scaleReverse;
    private final Main plugin = Main.getPlugin(Main.class);
    private ConfigurationManager configManager = new ConfigurationManager(plugin);
    private FileConfiguration config = configManager.getConfig();

    public int getRadiusLimit() {
        return playerRadiusLimit + 2;
    }

    public void setRadiusLimit(int newLimit) {
         playerRadiusLimit = newLimit;
         config.set("playerRadiusLimit", playerRadiusLimit);
         configManager.saveConfig();
    }
    public int getTntRadiusLimit() {
        return tntRadiusLimit + 2;
    }
    public void setTntRadiusLimit(int newLimit) {
        tntRadiusLimit = newLimit;
        config.set("tntRadiusLimit", tntRadiusLimit);
        configManager.saveConfig();
    }
    public int getCreeperRadiusLimit() {
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
    public ScaleReverse getScaleReverse() {
        return scaleReverse;
    }

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

        // Register the listeners
        getServer().getPluginManager().registerEvents(bucketListener, this);
        getServer().getPluginManager().registerEvents(barrierListener, this);
        getServer().getPluginManager().registerEvents(bedrockListener, this);
        getServer().getPluginManager().registerEvents(waterBucketListener, this);
        getServer().getPluginManager().registerEvents(tntListener, this);
        getServer().getPluginManager().registerEvents(creeperListener, this);

        // Register the StopWauh command with the listeners as arguments
        this.getCommand("stopwauh").setExecutor(new StopWauh(bucketListener, barrierListener, bedrockListener, waterBucketListener));
        this.getCommand("setradiuslimit").setExecutor(new SetRadiusLimitCommand(this));
        this.getCommand("setradiuslimit").setTabCompleter(new SetRadiusLimitCommand(this));
        this.getCommand("toggleremovalview").setExecutor(new ToggleRemovalView(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "kys");
    }
}
