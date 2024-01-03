package org.gecko.wauh.gui;

import de.tr7zw.changeme.nbtapi.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gecko.wauh.Main;
import org.gecko.wauh.data.ConfigurationManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigGUI implements Listener {

    public static final String ENABLE_BARRIER = "Enable Barrier";
    public static final String ENABLE_BEDROCK = "Enable Bedrock";
    public static final String ENABLE_TSUNAMI = "Enable Tsunami";
    public static final String ENABLE_CREEPER = "Enable Creeper";
    public static final String ENABLE_TNT = "Enable TNT";
    public static final String MANAGED = "This value is managed by the player radius limit.";
    public static final String BUCKET_ENABLED = "Bucket enabled";
    public static final String BARRIER_ENABLED = "Barrier enabled";
    public static final String BEDROCK_ENABLED = "Bedrock enabled";
    public static final String TSUNAMI_ENABLED = "Tsunami enabled";
    public static final String CREEPER_ENABLED = "Creeper enabled";
    public static final String TNT_ENABLED = "TNT enabled";
    private final Inventory gui;
    private static final int SIZE = 45;
    final ConfigurationManager configManager;
    FileConfiguration config;
    private final File configFile;
    private final Logger logger = Logger.getLogger(ConfigGUI.class.getName());
    private final Main plugin;
    private static final String DISABLE = "Disable";
    private static final String ENABLE = "Enable";
    private static final String ENABLE_BUCKET = "Enable Bucket";

    public ConfigGUI(Main plugin) {
        configManager = new ConfigurationManager(plugin);
        config = configManager.getConfig();
        this.gui = Bukkit.createInventory(null, SIZE, "Test (WIP)");
        File dir = new File("plugins/Wauh");
        this.configFile = new File(dir, "data.yml");
        this.plugin = plugin;

        fillBorders(createButtonItem(Material.STAINED_GLASS_PANE, "§r", (short) 5, null, null));
        // Initialize GUI content
        initializeGUI();
    }

    private void initializeGUI() {
        if (config.getInt(BUCKET_ENABLED) == 1) {
            gui.setItem(9 * 3 + 1, createButtonItem(Material.INK_SACK, DISABLE, (short) 10, null, ENABLE_BUCKET));
        } else {
            gui.setItem(9 * 3 + 1, createButtonItem(Material.INK_SACK, ENABLE, (short) 8, null, ENABLE_BUCKET));
        }

        if (config.getInt(BARRIER_ENABLED) == 1) {
            gui.setItem(9 * 3 + 2, createButtonItem(Material.INK_SACK, DISABLE, (short) 10, null, ENABLE_BARRIER));
        } else {
            gui.setItem(9 * 3 + 2, createButtonItem(Material.INK_SACK, ENABLE, (short) 8, null, ENABLE_BARRIER));
        }

        if (config.getInt(BEDROCK_ENABLED) == 1) {
            gui.setItem(9 * 3 + 3, createButtonItem(Material.INK_SACK, DISABLE, (short) 10, null, ENABLE_BEDROCK));
        } else {
            gui.setItem(9 * 3 + 3, createButtonItem(Material.INK_SACK, ENABLE, (short) 8, null, ENABLE_BEDROCK));
        }

        if (config.getInt(TSUNAMI_ENABLED) == 1) {
            gui.setItem(9 * 3 + 4, createButtonItem(Material.INK_SACK, DISABLE, (short) 10, null, ENABLE_TSUNAMI));
        } else {
            gui.setItem(9 * 3 + 4, createButtonItem(Material.INK_SACK, ENABLE, (short) 8, null, ENABLE_TSUNAMI));
        }

        if (config.getInt(CREEPER_ENABLED) == 1) {
            gui.setItem(9 * 3 + 5, createButtonItem(Material.INK_SACK, DISABLE, (short) 10, null, ENABLE_CREEPER));
        } else {
            gui.setItem(9 * 3 + 5, createButtonItem(Material.INK_SACK, ENABLE, (short) 8, null, ENABLE_CREEPER));
        }

        if (config.getInt(TNT_ENABLED) == 1) {
            gui.setItem(9 * 3 + 6, createButtonItem(Material.INK_SACK, DISABLE, (short) 10, null, ENABLE_TNT));
        } else {
            gui.setItem(9 * 3 + 6, createButtonItem(Material.INK_SACK, ENABLE, (short) 8, null, ENABLE_TNT));
        }

        gui.setItem(9 * 4 + 8, createButtonItem(Material.PAPER, ChatColor.RESET + "" + ChatColor.RED + "Reset config", (short) 0, null, "Reset"));
    }

    private void fillBorders(ItemStack borderItem) {
        // Fill top and bottom rows
        int size9 = SIZE / 9;
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, borderItem); // Top row
        }
        for (int i = 0; i < 8; i++) {
            gui.setItem(9 * (size9 - 1) + i, borderItem); // Bottom row
        }

        // Fill left and right columns
        for (int i = 0; i < (size9 - 1); i++) {
            int leftSlot = 9 * (i + 1);

            gui.setItem(leftSlot, borderItem); // Left column
        }
        for (int i =0; i < (size9 - 2); i++) {
            int rightSlot = 9 * (i + 2) - 1;

            gui.setItem(rightSlot, borderItem); // Right column
        }
    }


    private ItemStack createButtonItem(Material material, String name, short data, String lore, String ident) {
        List<String> loreToString;
        if (lore != null) {
            lore = ChatColor.RESET + lore;
            loreToString = Collections.singletonList(lore);
        } else {
            loreToString = null;
        }
        if (name != null) {
            name = ChatColor.RESET + name;
        }
        ItemStack item = new ItemStack(material, 1, data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(loreToString);
        item.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("Ident", ident);

        return nbtItem.getItem();
    }

    public void openGUI(Player player) {
        int playerLimit = plugin.getRadiusLimit() - 2;
        int creeperLimit = plugin.getCreeperRadiusLimit() - 2;
        int tntLimit = plugin.getTntRadiusLimit() - 2;
        gui.setItem(9 + 1, createButtonItem(Material.BUCKET, ChatColor.RESET + "Liquid removal", (short) 0, null, null));
        gui.setItem(9 * 2 + 1, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(playerLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + MANAGED, null));
        gui.setItem(9 + 2, createButtonItem(Material.BARRIER, ChatColor.RESET + "Surface removal", (short) 0, null, null));
        gui.setItem(9 * 2 + 2, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(playerLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + MANAGED, null));
        gui.setItem(9 + 3, createButtonItem(Material.BEDROCK, ChatColor.RESET + "All block removal", (short) 0, null, null));
        gui.setItem(9 * 2 + 3, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(playerLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + MANAGED, null));
        gui.setItem(9 + 4, createButtonItem(Material.WATER_BUCKET, ChatColor.RESET + "Tsunami", (short) 0, null, null));
        gui.setItem(9 * 2 + 4, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(playerLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + MANAGED, null));
        gui.setItem(9 + 5, createButtonItem(Material.SKULL_ITEM, ChatColor.RESET + "Custom creeper explosions", (short) 4, null, null));
        gui.setItem(9 * 2 + 5, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(creeperLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "This value is managed by the creeper radius limit.", null));
        gui.setItem(9 + 6, createButtonItem(Material.TNT, ChatColor.RESET + "Custom TNT explosions", (short) 0, null, null));
        gui.setItem(9 * 2 + 6, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(tntLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "This value is managed by the TNT radius limit.", null));
        player.openInventory(gui);
    }

    private void handleButtonClick(Player player, String identifier, short data, String configKey, int guiIndex, String enableMessage, String disableMessage) {
        boolean isEnabled = data == 8;

        config.set(configKey, isEnabled ? 1 : 0);
        configManager.saveConfig();

        gui.setItem(9 * 3 + guiIndex, createButtonItem(
                Material.INK_SACK,
                isEnabled ? DISABLE : ENABLE,
                (short) (isEnabled ? 10 : 8),
                null,
                identifier
        ));

        player.sendMessage(isEnabled ? disableMessage : enableMessage);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (isClickEventValid(event)) {
            handleItemClick((Player) event.getWhoClicked(), event.getCurrentItem());
        }
    }

    private boolean isClickEventValid(InventoryClickEvent event) {
        return event.getInventory().equals(gui) && event.getWhoClicked() instanceof Player;
    }

    private void handleItemClick(Player player, ItemStack clickedItem) {
        if (clickedItem != null && clickedItem.getType() == Material.INK_SACK) {
            NBTItem nbtItem = new NBTItem(clickedItem);
            String identifier = nbtItem.getString("Ident");
            short data = clickedItem.getDurability();

            if (handleButtonFeatures(player, identifier, data)) {
                return; // Button feature handling succeeded
            }

            if (clickedItem.getType() == Material.PAPER && (identifier.equalsIgnoreCase("Reset"))) {
                resetConfig(player);
            }
        }
    }

    private boolean handleButtonFeatures(Player player, String identifier, short data) {
        if (identifier.equalsIgnoreCase(ENABLE_BUCKET)) {
            handleButtonClick(player, identifier, data, BUCKET_ENABLED, 1, "Liquid removal enabled!", "Liquid removal disabled!");
            return true;
        } else if (identifier.equalsIgnoreCase(ENABLE_BARRIER)) {
            handleButtonClick(player, identifier, data, BARRIER_ENABLED, 2, "Surface removal enabled!", "Surface removal disabled!");
            return true;
        } else if (identifier.equalsIgnoreCase(ENABLE_BEDROCK)) {
            handleButtonClick(player, identifier, data, BEDROCK_ENABLED, 3, "All block removal enabled!", "All block removal disabled!");
            return true;
        } else if (identifier.equalsIgnoreCase(ENABLE_TSUNAMI)) {
            handleButtonClick(player, identifier, data, TSUNAMI_ENABLED, 4, "Tsunami enabled!", "Tsunami disabled!");
            return true;
        } else if (identifier.equalsIgnoreCase(ENABLE_CREEPER)) {
            handleButtonClick(player, identifier, data, CREEPER_ENABLED, 5, "Custom creeper explosions enabled!", "Custom creeper explosions disabled!");
            return true;
        } else if (identifier.equalsIgnoreCase(ENABLE_TNT)) {
            handleButtonClick(player, identifier, data, TNT_ENABLED, 6, "Custom TNT explosions enabled!", "Custom TNT explosions disabled!");
            return true;
        }
        return false; // No button feature handled
    }

    private void resetConfig(Player player) {
        try {
            // Create the data.yml file if it doesn't exist
            if (!configFile.exists()) {
                boolean fileCreated = configFile.createNewFile();
                if (!fileCreated) {
                    logger.log(Level.SEVERE, "Config file could not be created");
                } else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Config file created!");
                    FileWriter writer = getFileWriter();
                    writer.close();
                }
            } else {
                if (!isFileDeleted()) {
                    logger.log(Level.SEVERE, "Config file could not be deleted");
                    player.sendMessage(ChatColor.RED + "Config file could not be reset");
                } else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Config file deleted!");
                    boolean fileCreated = configFile.createNewFile();
                    if (!fileCreated) {
                        logger.log(Level.SEVERE, "Config file could not be created");
                        player.sendMessage(ChatColor.RED + "Config file could not be reset");
                    } else {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Config file created!");
                        FileWriter writer = getFileWriter();
                        writer.close();
                        player.sendMessage(ChatColor.GREEN + "Config reset!");
                    }
                }
            }

            this.config = YamlConfiguration.loadConfiguration(configFile);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Could not reset config file", ex);
        }
    }

    private boolean isFileDeleted() throws IOException {
        try {
            Files.delete(configFile.toPath());
            // Deletion successful
            return true;
        } catch (NoSuchFileException e) {
            // File does not exist, consider it deleted
            return true;
        } catch (IOException e) {
            // Directory is not empty, consider it not deleted or Unable to delete file for various causes, consider it not deleted
            return false;
        }
    }

    private FileWriter getFileWriter() throws IOException {
        try (FileWriter writer = new FileWriter(configFile)
        ) {
            writer.write("playerRadiusLimit: 20\n");
            writer.write("tntRadiusLimit: 5\n");
            writer.write("creeperRadiusLimit: 5\n");
            writer.write("Bucket enabled: 1\n");
            writer.write("Barrier enabled: 1\n");
            writer.write("Bedrock enabled: 1\n");
            writer.write("Tsunami enabled: 1\n");
            writer.write("Creeper enabled: 0\n");
            writer.write("TNT enabled: 1\n");
            return writer;
        } catch (IOException e) {
            throw new IOException("Unable to create FileWriter", e);
        }
    }

}