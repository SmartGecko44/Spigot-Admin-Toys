package org.gecko.wauh.gui;

import de.tr7zw.nbtapi.NBTItem;
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

import java.lang.reflect.Array;
import java.util.*;

public class ConfigGUI implements Listener {

    private JavaPlugin plugin;
    private final Inventory gui;
    private final int size = 45;
    private final int size9 = size / 9;

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
        gui.setItem(9 + 2, createButtonItem(Material.BARRIER, "Surface Removal", (short) 0, null)); // Gray dye for disable
        gui.setItem(9 + 3, createButtonItem(Material.BEDROCK, "All block removal", (short) 0, null)); // Gray dye for disable
        gui.setItem(9 + 4, createButtonItem(Material.WATER_BUCKET, "Tsunami", (short) 0, null)); // Gray dye for disable
        gui.setItem(9 + 5, createButtonItem(Material.SKULL_ITEM, "Custom creeper explosions", (short) 4, null)); // Gray dye for disable
        gui.setItem(9 + 6, createButtonItem(Material.TNT, "Custom TNT explosions", (short) 0, null)); // Gray dye for disable
        gui.setItem(9 * 3 + 1, createButtonItem(Material.INK_SACK, "Enable", (short) 10, "Enable Bucket"));
    }

    private void fillBorders(ItemStack borderItem) {
        // Fill top and bottom rows
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
        nbtItem.setString("Ident:", ident);

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

                // Handle button clicks
                if (clickedItem.getType() == Material.INK_SACK) {
                    short data = clickedItem.getDurability();

                    if (data == 10) {
                        // Green dye (Enable button) clicked
                        // Handle enabling logic here
                        player.sendMessage("Enabled!");
                    } else if (data == 8) {
                        // Gray dye (Disable button) clicked
                        // Handle disabling logic here
                        player.sendMessage("Disabled!");
                    }
                }
            }
        }
    }
}
