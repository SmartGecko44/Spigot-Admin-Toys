package org.gecko.wauh.gui;

import de.tr7zw.changeme.nbtapi.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigGUI implements Listener {

    private JavaPlugin plugin;
    private final Inventory gui;
    private final int size = 45;

    public ConfigGUI(JavaPlugin plugin) {
        this.plugin = plugin;
        this.gui = Bukkit.createInventory(null, size, "Test (WIP)");

        fillBorders(createButtonItem(Material.STAINED_GLASS_PANE, "\n", (short) 5, null));
        // Initialize GUI content
        initializeGUI();
    }

    private void initializeGUI() {
        // Add buttons or other elements to the GUI
        // For simplicity, let's add two buttons: Enable and Disable
        gui.setItem(9 + 1, createButtonItem(Material.BUCKET, "Liquid removal", (short) 0, null)); // Green dye for enable
        gui.setItem(9 + 2, createButtonItem(Material.BARRIER, "Surface removal", (short) 0, null)); // Gray dye for disable
        gui.setItem(9 + 3, createButtonItem(Material.BEDROCK, "All block removal", (short) 0, null)); // Gray dye for disable
        gui.setItem(9 + 4, createButtonItem(Material.WATER_BUCKET, "Tsunami", (short) 0, null)); // Gray dye for disable
        gui.setItem(9 + 5, createButtonItem(Material.SKULL_ITEM, "Custom creeper explosions", (short) 4, null)); // Gray dye for disable
        gui.setItem(9 + 6, createButtonItem(Material.TNT, "Custom TNT explosions", (short) 0, null)); // Gray dye for disable
        gui.setItem(9 * 3 + 1, createButtonItem(Material.INK_SACK, "Enable", (short) 10, "Enable Bucket"));
        gui.setItem(9 * 3 + 2, createButtonItem(Material.INK_SACK, "Enable", (short) 10, "Enable Barrier"));
        gui.setItem(9 * 3 + 3, createButtonItem(Material.INK_SACK, "Enable", (short) 10, "Enable Bedrock"));
        gui.setItem(9 * 3 + 4, createButtonItem(Material.INK_SACK, "Enable", (short) 10, "Enable Tsunami"));
        gui.setItem(9 * 3 + 5, createButtonItem(Material.INK_SACK, "Enable", (short) 10, "Enable Creeper"));
        gui.setItem(9 * 3 + 6, createButtonItem(Material.INK_SACK, "Enable", (short) 10, "Enable TNT"));
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
                        gui.setItem(9 * 3 + 1, createButtonItem(Material.INK_SACK, "Disable", (short) 8, "Enable Bucket"));
                        player.sendMessage("Liquid removal enabled!");
                    } else if (identifier.equalsIgnoreCase("Enable Bucket") && data == 10) {
                        gui.setItem(9 * 3 + 1, createButtonItem(Material.INK_SACK, "Enable", (short) 10, "Enable Bucket"));
                    }
                    if (identifier.equalsIgnoreCase("Enable Barrier") && data == 8) {
                        player.sendMessage("Surface removal enabled!");
                    } else if (identifier.equalsIgnoreCase("Enable Barrier") && data == 10) {
                    } else if (identifier.equalsIgnoreCase("Enable Bedrock")) {
                        player.sendMessage("All block removal enabled!");
                    } else if (identifier.equalsIgnoreCase("Enable Tsunami")) {
                        player.sendMessage("Tsunami enabled!");
                    } else if (identifier.equalsIgnoreCase("Enable Creeper")) {
                        player.sendMessage("Custom creeper explosions enabled!");
                    } else if (identifier.equalsIgnoreCase("Enable TNT")) {
                        player.sendMessage("Custom TNT explosions enabled!");
                    }
                }
            }
        }
    }
}