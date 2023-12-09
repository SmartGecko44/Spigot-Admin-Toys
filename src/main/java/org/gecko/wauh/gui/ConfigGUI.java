package org.gecko.wauh.gui;

import de.tr7zw.changeme.nbtapi.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gecko.wauh.Main;
import org.gecko.wauh.data.ConfigurationManager;

public class ConfigGUI implements Listener {

    private final Inventory gui;
    private final int size = 45;
    ConfigurationManager configManager;
    FileConfiguration config;

    public ConfigGUI() {
        configManager = new ConfigurationManager(Main.getPlugin(Main.class));
        config = configManager.getConfig();
        this.gui = Bukkit.createInventory(null, size, "Test (WIP)");

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
    }

    private void fillBorders(ItemStack borderItem) {
        // Fill top and bottom rows
        int size9 = size / 9;
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, borderItem); // Top row
            gui.setItem(9 * (size9 - 1) + i, borderItem); // Bottom row
        }

        // Fill left and right columns
        for (int i = 0; i < (size9 - 1); i++) {
            int leftSlot = 9 * (i + 1);
            int rightSlot = 9 * (i + 2) - 1;

            gui.setItem(leftSlot, borderItem); // Left column
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

            if (clickedItem != null) {
                Player player = (Player) event.getWhoClicked();

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
                    } else if (identifier.equalsIgnoreCase("Enable Bucket") && data == 10) {
                        config.set("Bucket enabled", 0);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 1, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, "Enable Bucket"));
                        player.sendMessage("Liquid removal disabled!");
                    }

                    if (identifier.equalsIgnoreCase("Enable Barrier") && data == 8) {
                        config.set("Barrier enabled", 1);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 2, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, "Enable Barrier"));
                        player.sendMessage("Surface removal enabled!");
                    } else if (identifier.equalsIgnoreCase("Enable Barrier") && data == 10) {
                        config.set("Barrier enabled", 0);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 2, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, "Enable Barrier"));
                        player.sendMessage("Surface removal disabled!");
                    }

                    if (identifier.equalsIgnoreCase("Enable Bedrock") && data == 8) {
                        config.set("Bedrock enabled", 1);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 3, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, "Enable Bedrock"));
                        player.sendMessage("All block removal enabled!");
                    } else if (identifier.equalsIgnoreCase("Enable Bedrock") && data == 10) {
                        config.set("Bedrock enabled", 0);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 3, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, "Enable Bedrock"));
                        player.sendMessage("All block removal disabled!");
                    }

                    if (identifier.equalsIgnoreCase("Enable Tsunami") && data == 8) {
                        config.set("Tsunami enabled", 1);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 4, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, "Enable Tsunami"));
                        player.sendMessage("Tsunami enabled!");
                    } else if (identifier.equalsIgnoreCase("Enable Tsunami") && data == 10) {
                        config.set("Tsunami enabled", 0);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 4, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, "Enable Tsunami"));
                        player.sendMessage("Tsunami disabled!");
                    }

                    if (identifier.equalsIgnoreCase("Enable Creeper") && data == 8) {
                        config.set("Creeper enabled", 1);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 5, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, "Enable Creeper"));
                        player.sendMessage("Custom creeper explosions enabled!");
                    } else if (identifier.equalsIgnoreCase("Enable Creeper") && data == 10) {
                        config.set("Creeper enabled", 0);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 5, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, "Enable Creeper"));
                        player.sendMessage("Custom creeper explosions disabled!");
                    }

                    if (identifier.equalsIgnoreCase("Enable TNT") && data == 8) {
                        config.set("TNT enabled", 1);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 6, createButtonItem(Material.INK_SACK, "§rDisable", (short) 10, "Enable TNT"));
                        player.sendMessage("Custom TNT explosions enabled!");
                    } else if (identifier.equalsIgnoreCase("Enable TNT") && data == 10) {
                        config.set("TNT enabled", 0);
                        configManager.saveConfig();
                        gui.setItem(9 * 3 + 6, createButtonItem(Material.INK_SACK, "§rEnable", (short) 8, "Enable TNT"));
                        player.sendMessage("Custom TNT explosions disabled!");
                    }
                }
            }
        }
    }
}