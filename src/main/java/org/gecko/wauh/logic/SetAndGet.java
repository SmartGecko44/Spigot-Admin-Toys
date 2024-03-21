package org.gecko.wauh.logic;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.gecko.wauh.Main;
import org.gecko.wauh.data.ConfigurationManager;
import org.gecko.wauh.enchantments.logic.EnchantmentHandler;
import org.gecko.wauh.listeners.*;

public class SetAndGet {

    private final EnchantmentHandler enchantmentHandler = new EnchantmentHandler();
    private int playerRadiusLimit;
    private int tntRadiusLimit;
    private int creeperRadiusLimit;
    private boolean showRemoval = true;
    ConfigurationManager configManager;
    FileConfiguration config;
    private final BucketListener bucketListener;
    private final BarrierListener barrierListener;
    private final BedrockListener bedrockListener;
    private final WaterBucketListener waterBucketListener;
    private final TNTListener tntListener;
    private final CreeperListener creeperListener;
    private final IterateBlocks iterateBlocks;

    public SetAndGet(ConfigurationManager configurationManager) {
        this.configManager = configurationManager;
        this.tntListener = new TNTListener(JavaPlugin.getPlugin(Main.class), this);
        this.creeperListener = new CreeperListener(JavaPlugin.getPlugin(Main.class), this);
        this.bucketListener = new BucketListener(this);
        this.barrierListener = new BarrierListener(this);
        this.bedrockListener = new BedrockListener(this);
        this.waterBucketListener = new WaterBucketListener(this);
        this.iterateBlocks = new IterateBlocks();

    }

    public int getRadiusLimit() {
        config = configManager.getConfig();
        playerRadiusLimit = config.getInt("playerRadiusLimit", playerRadiusLimit);
        return playerRadiusLimit + 2;
    }

    public void setRadiusLimit(int newLimit) {
        playerRadiusLimit = newLimit;
        config.set("playerRadiusLimit", playerRadiusLimit);
        configManager.saveConfig();
    }

    public int getTntRadiusLimit() {
        config = configManager.getConfig();
        tntRadiusLimit = config.getInt("tntRadiusLimit", tntRadiusLimit);
        return tntRadiusLimit + 2;
    }

    public void setTntRadiusLimit(int newLimit) {
        tntRadiusLimit = newLimit;
        config.set("tntRadiusLimit", tntRadiusLimit);
        configManager.saveConfig();
    }

    public int getCreeperRadiusLimit() {
        config = configManager.getConfig();
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
