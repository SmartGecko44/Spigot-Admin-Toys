package org.gecko.spigotadmintoys.logic;

import org.bukkit.configuration.file.FileConfiguration;
import org.gecko.spigotadmintoys.data.ConfigurationManager;
import org.gecko.spigotadmintoys.enchantments.logic.EnchantmentHandler;
import org.gecko.spigotadmintoys.listeners.*;

public class SetAndGet {

    public static final String REMOVAL_VISIBLE = "Removal visible";
    private final EnchantmentHandler enchantmentHandler = new EnchantmentHandler();
    private int playerRadiusLimit;
    private int tntRadiusLimit;
    private int creeperRadiusLimit;
    private final ConfigurationManager configManager;
    FileConfiguration config;
    private final BucketListener bucketListener;
    private final BarrierListener barrierListener;
    private final BedrockListener bedrockListener;
    private final WaterBucketListener waterBucketListener;
    private final TNTListener tntListener;
    private final CreeperListener creeperListener;
    private final IterateBlocks iterateBlocks;
    private final Scale scale;

    public SetAndGet(ConfigurationManager configurationManager) {
        this.configManager = configurationManager;
        this.tntListener = new TNTListener(configManager);
        this.creeperListener = new CreeperListener(configManager);
        this.bedrockListener = new BedrockListener(this);
        this.bucketListener = new BucketListener(this);
        this.barrierListener = new BarrierListener(this);
        this.waterBucketListener = new WaterBucketListener(this);
        this.iterateBlocks = new IterateBlocks();
        this.scale = new Scale(this);
    }

    public int getRadiusLimit() {
        config = getConfigManager().getConfig();
        playerRadiusLimit = config.getInt("playerRadiusLimit", playerRadiusLimit);
        return playerRadiusLimit + 2;
    }

    public void setRadiusLimit(int newLimit) {
        config = getConfigManager().getConfig();
        config.set("playerRadiusLimit", newLimit);
        getConfigManager().saveConfig();
    }

    public int getTntRadiusLimit() {
        config = getConfigManager().getConfig();
        tntRadiusLimit = config.getInt("tntRadiusLimit", tntRadiusLimit);
        return tntRadiusLimit + 2;
    }

    public void setTntRadiusLimit(int newLimit) {
        config = configManager.getConfig();
        config.set("tntRadiusLimit", newLimit);
        getConfigManager().saveConfig();
    }

    public int getCreeperRadiusLimit() {
        config = getConfigManager().getConfig();
        creeperRadiusLimit = config.getInt("creeperRadiusLimit", creeperRadiusLimit);
        return creeperRadiusLimit + 2;
    }

    public void setCreeperLimit(int newLimit) {
        config = configManager.getConfig();
        config.set("creeperRadiusLimit", newLimit);
        getConfigManager().saveConfig();
    }

    public boolean getShowRemoval() {
        config = getConfigManager().getConfig();
        return config.getBoolean(REMOVAL_VISIBLE);
    }

    public void toggleRemovalView() {
        config = configManager.getConfig();
        config.set(REMOVAL_VISIBLE, !config.getBoolean(REMOVAL_VISIBLE));
        configManager.saveConfig();
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

    public Scale getScale() {
        return scale;
    }

    public ConfigurationManager getConfigManager() {
        return configManager;
    }
}
