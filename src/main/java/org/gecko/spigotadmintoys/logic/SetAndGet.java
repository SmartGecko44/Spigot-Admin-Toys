package org.gecko.spigotadmintoys.logic;

import org.bukkit.configuration.file.FileConfiguration;
import org.gecko.spigotadmintoys.data.ConfigurationManager;
import org.gecko.spigotadmintoys.enchantments.logic.EnchantmentHandler;
import org.gecko.spigotadmintoys.gui.ConfigGUI;
import org.gecko.spigotadmintoys.gui.logic.CreateButtonItem;
import org.gecko.spigotadmintoys.gui.presets.config.Assign;
import org.gecko.spigotadmintoys.listeners.*;

public class SetAndGet {

    public static final String REMOVAL_VISIBLE = "Removal visible";
    private final EnchantmentHandler enchantmentHandler = new EnchantmentHandler();
    private final ConfigurationManager configManager;
    private final BucketListener bucketListener;
    private final BarrierListener barrierListener;
    private final BedrockListener bedrockListener;
    private final WaterBucketListener waterBucketListener;
    private final SphereMaker sphereMaker;
    private final TNTListener tntListener;
    private final CreeperListener creeperListener;
    private final IterateBlocks iterateBlocks;
    private final Scale scale;
    private final CreateButtonItem createButtonItem;
    private final Assign assign;
    private final ConfigGUI configGUI;
    FileConfiguration config;
    private int playerRadiusLimit;
    private int tntRadiusLimit;
    private int creeperRadiusLimit;

    public SetAndGet() {
        this.configManager = new ConfigurationManager(this);
        this.tntListener = new TNTListener(configManager);
        this.creeperListener = new CreeperListener(configManager);
        this.bedrockListener = new BedrockListener(this);
        this.bucketListener = new BucketListener(this);
        this.barrierListener = new BarrierListener(this);
        this.waterBucketListener = new WaterBucketListener(this);
        this.sphereMaker = new SphereMaker(this);
        this.iterateBlocks = new IterateBlocks();
        this.scale = new Scale(this);
        this.createButtonItem = new CreateButtonItem();
        this.assign = new Assign(this);
        this.configGUI = new ConfigGUI(this);
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

    public SphereMaker getSphereMaker() {
        return sphereMaker;
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

    public CreateButtonItem getCreateButtonItem() {
        return createButtonItem;
    }

    public Assign getAssign() {
        return assign;
    }

    public ConfigGUI getConfigGUI() {
        return configGUI;
    }
}
