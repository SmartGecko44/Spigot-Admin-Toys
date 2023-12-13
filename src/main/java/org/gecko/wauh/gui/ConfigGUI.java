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
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigGUI implements Listener {

    private final Inventory gui;
    private final int size = 45;
    final ConfigurationManager configManager;
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

        fillBorders(createButtonItem(Material.STAINED_GLASS_PANE, "§r", (short) 5, null, null));
        // Initialize GUI content
        initializeGUI();
    }

    private void initializeGUI() {
        if (config.getInt("Bucket enabled") == 1) {
            gui.setItem(9 * 3 + 1, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, null, "Enable Bucket"));
        } else {
            gui.setItem(9 * 3 + 1, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, null, "Enable Bucket"));
        }

        if (config.getInt("Barrier enabled") == 1) {
            gui.setItem(9 * 3 + 2, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, null, "Enable Barrier"));
        } else {
            gui.setItem(9 * 3 + 2, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, null, "Enable Barrier"));
        }

        if (config.getInt("Bedrock enabled") == 1) {
            gui.setItem(9 * 3 + 3, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, null, "Enable Bedrock"));
        } else {
            gui.setItem(9 * 3 + 3, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, null, "Enable Bedrock"));
        }

        if (config.getInt("Tsunami enabled") == 1) {
            gui.setItem(9 * 3 + 4, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, null, "Enable Tsunami"));
        } else {
            gui.setItem(9 * 3 + 4, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, null, "Enable Tsunami"));
        }

        if (config.getInt("Creeper enabled") == 1) {
            gui.setItem(9 * 3 + 5, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, null, "Enable Creeper"));
        } else {
            gui.setItem(9 * 3 + 5, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, null, "Enable Creeper"));
        }

        if (config.getInt("TNT enabled") == 1) {
            gui.setItem(9 * 3 + 6, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, null, "Enable TNT"));
        } else {
            gui.setItem(9 * 3 + 6, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, null, "Enable TNT"));
        }

        gui.setItem(9 * 4 + 8, createButtonItem(Material.PAPER, ChatColor.RESET + "" + ChatColor.RED + "Reset config", (short) 0, null, "Reset"));
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


    private ItemStack createButtonItem(Material material, String name, short data, String lore, String ident) {
        List<String> loreToString;
        if (lore != null) {
            loreToString = Collections.singletonList(lore);
        } else {
            loreToString = null;
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
        gui.setItem(9 * 2 + 1, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(playerLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "This value is managed by the player radius limit.", null));
        gui.setItem(9 + 2, createButtonItem(Material.BARRIER, ChatColor.RESET + "Surface removal", (short) 0, null, null));
        gui.setItem(9 * 2 + 2, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(playerLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "This value is managed by the player radius limit.", null));
        gui.setItem(9 + 3, createButtonItem(Material.BEDROCK, ChatColor.RESET + "All block removal", (short) 0, null, null));
        gui.setItem(9 * 2 + 3, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(playerLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "This value is managed by the player radius limit.", null));
        gui.setItem(9 + 4, createButtonItem(Material.WATER_BUCKET, ChatColor.RESET + "Tsunami", (short) 0, null, null));
        gui.setItem(9 * 2 + 4, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(playerLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "This value is managed by the player radius limit.", null));
        gui.setItem(9 + 5, createButtonItem(Material.SKULL_ITEM, ChatColor.RESET + "Custom creeper explosions", (short) 4, null, null));
        gui.setItem(9 * 2 + 5, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(creeperLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "This value is managed by the creeper radius limit.", null));
        gui.setItem(9 + 6, createButtonItem(Material.TNT, ChatColor.RESET + "Custom TNT explosions", (short) 0, null, null));
        gui.setItem(9 * 2 + 6, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(tntLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "This value is managed by the TNT radius limit.", null));
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
                        gui.setItem(9 * 3 + 1, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, null, "Enable Bucket"));
                        player.sendMessage("Liquid removal enabled!");
                        return;
                    } else if (identifier.equalsIgnoreCase("Enable Bucket") && data == 10) {
                        config.set("Bucket enabled", 0);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 1, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, null, "Enable Bucket"));
                        player.sendMessage("Liquid removal disabled!");
                        return;
                    }

                    if (identifier.equalsIgnoreCase("Enable Barrier") && data == 8) {
                        config.set("Barrier enabled", 1);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 2, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, null, "Enable Barrier"));
                        player.sendMessage("Surface removal enabled!");
                        return;
                    } else if (identifier.equalsIgnoreCase("Enable Barrier") && data == 10) {
                        config.set("Barrier enabled", 0);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 2, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, null, "Enable Barrier"));
                        player.sendMessage("Surface removal disabled!");
                        return;
                    }

                    if (identifier.equalsIgnoreCase("Enable Bedrock") && data == 8) {
                        config.set("Bedrock enabled", 1);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 3, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, null, "Enable Bedrock"));
                        player.sendMessage("All block removal enabled!");
                        return;
                    } else if (identifier.equalsIgnoreCase("Enable Bedrock") && data == 10) {
                        config.set("Bedrock enabled", 0);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 3, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, null, "Enable Bedrock"));
                        player.sendMessage("All block removal disabled!");
                        return;
                    }

                    if (identifier.equalsIgnoreCase("Enable Tsunami") && data == 8) {
                        config.set("Tsunami enabled", 1);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 4, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, null, "Enable Tsunami"));
                        player.sendMessage("Tsunami enabled!");
                        return;
                    } else if (identifier.equalsIgnoreCase("Enable Tsunami") && data == 10) {
                        config.set("Tsunami enabled", 0);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 4, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, null, "Enable Tsunami"));
                        player.sendMessage("Tsunami disabled!");
                        return;
                    }

                    if (identifier.equalsIgnoreCase("Enable Creeper") && data == 8) {
                        config.set("Creeper enabled", 1);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 5, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, null, "Enable Creeper"));
                        player.sendMessage("Custom creeper explosions enabled!");
                        return;
                    } else if (identifier.equalsIgnoreCase("Enable Creeper") && data == 10) {
                        config.set("Creeper enabled", 0);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 5, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, null, "Enable Creeper"));
                        player.sendMessage("Custom creeper explosions disabled!");
                        return;
                    }

                    if (identifier.equalsIgnoreCase("Enable TNT") && data == 8) {
                        config.set("TNT enabled", 1);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 6, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, null, "Enable TNT"));
                        player.sendMessage("Custom TNT explosions enabled!");
                        return;
                    } else if (identifier.equalsIgnoreCase("Enable TNT") && data == 10) {
                        config.set("TNT enabled", 0);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 6, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, null, "Enable TNT"));
                        player.sendMessage("Custom TNT explosions disabled!");
                        return;
                    }
                }

                if (clickedItem.getType() == Material.PAPER) {
                    if (identifier.equalsIgnoreCase("Reset")) {
                        resetConfig(player);
                    }
                }

            }
        }
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

    private FileWriter getFileWriter() throws IOException {
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
        return writer;
    }

}