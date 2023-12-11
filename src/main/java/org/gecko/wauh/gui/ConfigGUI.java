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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigGUI implements Listener {

    private final Inventory gui;
    private final int size = 45;
    ConfigurationManager configManager;
    FileConfiguration config;
    private final File configFile;
    private final Logger logger = Logger.getLogger(Main.class.getName());
    private final Main plugin;

    public ConfigGUI(Main plugin) {
        configManager = new ConfigurationManager(Main.getPlugin(Main.class));
        config = configManager.getConfig();
        this.gui = Bukkit.createInventory(null, size, "Test (WIP)");
        File dir = new File("plugins/Wauh");
        this.configFile = new File(dir, "data.yml");
        this.plugin = plugin;

        fillBorders(createButtonItem(Material.STAINED_GLASS_PANE, "§r", (short) 5, null));
        // Initialize GUI content
        initializeGUI();
    }

    private void initializeGUI() {
        // Add buttons or other elements to the GUI
        // For simplicity, let's add two buttons: Enable and Disable
        gui.setItem(9 + 1, createButtonItem(Material.BUCKET, "§rLiquid removal", (short) 0, null)); // Green dye for enable
        gui.setItem(9 + 2, createButtonItem(Material.BARRIER, "§rSurface removal", (short) 0, null)); // Gray dye for disable
        gui.setItem(9 + 3, createButtonItem(Material.BEDROCK, "§rAll block removal", (short) 0, null)); // Gray dye for disable
        gui.setItem(9 + 4, createButtonItem(Material.WATER_BUCKET, "§rTsunami", (short) 0, null)); // Gray dye for disable
        gui.setItem(9 + 5, createButtonItem(Material.SKULL_ITEM, "§rCustom creeper explosions", (short) 4, null)); // Gray dye for disable
        gui.setItem(9 + 6, createButtonItem(Material.TNT, "§rCustom TNT explosions", (short) 0, null)); // Gray dye for disable
        if (config.getInt("Bucket enabled") == 1) {
            gui.setItem(9 * 3 + 1, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, "Enable Bucket"));
        } else {
            gui.setItem(9 * 3 + 1, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, "Enable Bucket"));
        }

        if (config.getInt("Barrier enabled") == 1) {
            gui.setItem(9 * 3 + 2, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, "Enable Barrier"));
        } else {
            gui.setItem(9 * 3 + 2, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, "Enable Barrier"));
        }

        if (config.getInt("Bedrock enabled") == 1) {
            gui.setItem(9 * 3 + 3, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, "Enable Bedrock"));
        } else {
            gui.setItem(9 * 3 + 3, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, "Enable Bedrock"));
        }

        if (config.getInt("Tsunami enabled") == 1) {
            gui.setItem(9 * 3 + 4, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, "Enable Tsunami"));
        } else {
            gui.setItem(9 * 3 + 4, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, "Enable Tsunami"));
        }

        if (config.getInt("Creeper enabled") == 1) {
            gui.setItem(9 * 3 + 5, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, "Enable Creeper"));
        } else {
            gui.setItem(9 * 3 + 5, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, "Enable Creeper"));
        }

        if (config.getInt("TNT enabled") == 1) {
            gui.setItem(9 * 3 + 6, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, "Enable TNT"));
        } else {
            gui.setItem(9 * 3 + 6, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, "Enable TNT"));
        }

        gui.setItem(9 * 4 + 8, createButtonItem(Material.PAPER, ChatColor.RESET + "" + ChatColor.RED + "Reset config", (short) 0, "Reset"));
    }

    private void fillBorders(ItemStack borderItem) {
        // Fill top and bottom rows
        int size9 = size / 9;
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


    private ItemStack createButtonItem(Material material, String name, short data, String ident) {
        ItemStack item = new ItemStack(material, 1, data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("Ident", ident);

        return nbtItem.getItem();
    }

    public void openGUI(Player player) {
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().equals(gui) && event.getWhoClicked() instanceof Player) {
            event.setCancelled(true); // Prevent item moving or other inventory actions

            ItemStack clickedItem = event.getCurrentItem();
            Player player = ((Player) event.getWhoClicked()).getPlayer();

            if (clickedItem != null) {

                NBTItem nbtItem = new NBTItem(clickedItem);
                String identifier = nbtItem.getString("Ident");
                short data = clickedItem.getDurability();

                // Handle button clicks
                if (clickedItem.getType() == Material.INK_SACK) {
                    if (identifier.equalsIgnoreCase("Enable Bucket") && data == 8) {
                        config.set("Bucket enabled", 1);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 1, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, "Enable Bucket"));
                        player.sendMessage("Liquid removal enabled!");
                        return;
                    } else if (identifier.equalsIgnoreCase("Enable Bucket") && data == 10) {
                        config.set("Bucket enabled", 0);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 1, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, "Enable Bucket"));
                        player.sendMessage("Liquid removal disabled!");
                        return;
                    }

                    if (identifier.equalsIgnoreCase("Enable Barrier") && data == 8) {
                        config.set("Barrier enabled", 1);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 2, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, "Enable Barrier"));
                        player.sendMessage("Surface removal enabled!");
                        return;
                    } else if (identifier.equalsIgnoreCase("Enable Barrier") && data == 10) {
                        config.set("Barrier enabled", 0);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 2, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, "Enable Barrier"));
                        player.sendMessage("Surface removal disabled!");
                        return;
                    }

                    if (identifier.equalsIgnoreCase("Enable Bedrock") && data == 8) {
                        config.set("Bedrock enabled", 1);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 3, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, "Enable Bedrock"));
                        player.sendMessage("All block removal enabled!");
                        return;
                    } else if (identifier.equalsIgnoreCase("Enable Bedrock") && data == 10) {
                        config.set("Bedrock enabled", 0);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 3, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, "Enable Bedrock"));
                        player.sendMessage("All block removal disabled!");
                        return;
                    }

                    if (identifier.equalsIgnoreCase("Enable Tsunami") && data == 8) {
                        config.set("Tsunami enabled", 1);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 4, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, "Enable Tsunami"));
                        player.sendMessage("Tsunami enabled!");
                        return;
                    } else if (identifier.equalsIgnoreCase("Enable Tsunami") && data == 10) {
                        config.set("Tsunami enabled", 0);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 4, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, "Enable Tsunami"));
                        player.sendMessage("Tsunami disabled!");
                        return;
                    }

                    if (identifier.equalsIgnoreCase("Enable Creeper") && data == 8) {
                        config.set("Creeper enabled", 1);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 5, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, "Enable Creeper"));
                        player.sendMessage("Custom creeper explosions enabled!");
                        return;
                    } else if (identifier.equalsIgnoreCase("Enable Creeper") && data == 10) {
                        config.set("Creeper enabled", 0);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 5, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, "Enable Creeper"));
                        player.sendMessage("Custom creeper explosions disabled!");
                        return;
                    }

                    if (identifier.equalsIgnoreCase("Enable TNT") && data == 8) {
                        config.set("TNT enabled", 1);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 6, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, "Enable TNT"));
                        player.sendMessage("Custom TNT explosions enabled!");
                        return;
                    } else if (identifier.equalsIgnoreCase("Enable TNT") && data == 10) {
                        config.set("TNT enabled", 0);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 6, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, "Enable TNT"));
                        player.sendMessage("Custom TNT explosions disabled!");
                        return;
                    }
                }

                if (clickedItem.getType() == Material.PAPER) {
                    if (identifier.equalsIgnoreCase("Reset")) {
                        resetConfig(plugin, player);
                    }
                }

            }
        }
    }

    private void resetConfig(Main plugin, Player player) {
        try {
            // Create the data.yml file if it doesn't exist
            if (!configFile.exists()) {
                boolean fileCreated = configFile.createNewFile();
                if (!fileCreated) {
                    logger.log(Level.SEVERE, "Config file could not be created");
                } else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Config file created!");
                    FileWriter writer = new FileWriter(configFile);
                    writer.write("playerRadiusLimit: 20\n");
                    writer.write("tntRadiusLimit: 5\n");
                    writer.write("creeperRadiusLimit: 5\n");
                    writer.write("Bucket enabled: 1\n");
                    writer.write("Barrier enabled: 1\n");
                    writer.write("Bedrock enabled: 1\n");
                    writer.write("Tsunami enabled: 1\n");
                    writer.write("Creeper enabled: 0\n");
                    writer.write("TNT enabled: 1\n");
                    writer.close();
                }
            } else {
                boolean fileDeleted = configFile.delete();
                if (!fileDeleted) {
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
                        FileWriter writer = new FileWriter(configFile);
                        writer.write("playerRadiusLimit: 20\n");
                        writer.write("tntRadiusLimit: 5\n");
                        writer.write("creeperRadiusLimit: 5\n");
                        writer.write("Bucket enabled: 1\n");
                        writer.write("Barrier enabled: 1\n");
                        writer.write("Bedrock enabled: 1\n");
                        writer.write("Tsunami enabled: 1\n");
                        writer.write("Creeper enabled: 0\n");
                        writer.write("TNT enabled: 1\n");
                        writer.close();
                        player.sendMessage(ChatColor.GREEN + "Config reset!");
                    }
                }
            }

            this.config = YamlConfiguration.loadConfiguration(configFile);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not reset config file", ex);
        }
    }

}