package org.gecko.spigotadmintoys.gui;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.gecko.spigotadmintoys.data.ConfigurationManager;
import org.gecko.spigotadmintoys.logic.SetAndGet;

import java.util.function.Function;

public class ConfigGUI implements Listener {

    public static final String ENABLE_BUCKET = "Enable Bucket";
    public static final String ENABLE_BARRIER = "Enable Barrier";
    public static final String ENABLE_BEDROCK = "Enable Bedrock";
    public static final String ENABLE_SPHERE = "Enable Sphere";
    public static final String ENABLE_TSUNAMI = "Enable Tsunami";
    public static final String ENABLE_CREEPER = "Enable Creeper";
    public static final String ENABLE_TNT = "Enable TNT";
    public static final String REMOVAL_VISIBLE = "Removal visible";
    public static final String MANAGED = "This value is managed by the player radius limit.";
    public static final String BUCKET_ENABLED = "Bucket enabled";
    public static final String BARRIER_ENABLED = "Barrier enabled";
    public static final String BEDROCK_ENABLED = "Bedrock enabled";
    public static final String SPHERE_ENABLED = "Sphere enabled";
    public static final String TSUNAMI_ENABLED = "Tsunami enabled";
    public static final String CREEPER_ENABLED = "Creeper enabled";
    public static final String TNT_ENABLED = "TNT enabled";
    public static final String DISABLE = ChatColor.RED + "Disable";
    public static final String ENABLE = ChatColor.GREEN + "Enable";
    final ConfigurationManager configManager;
    private final SetAndGet setAndGet;
    private final FileConfiguration config;
    private Inventory gui;
    private int currentPage = 0;


    public ConfigGUI(SetAndGet setAndGet) {
        configManager = setAndGet.getConfigManager();
        config = configManager.getConfig();
        this.setAndGet = setAndGet;
    }

    public void generateGUI(int page) {
        this.gui = Bukkit.createInventory(null, 45, "Test (WIP) Page " + (page + 1));

        fillBorders(createButtonItem(Material.STAINED_GLASS_PANE, "§r", (short) 5, null, null), 45);

        if (page > 0) {
            gui.setItem(9 * 4, createButtonItem(Material.ARROW, "Previous Page", (short) 0, null, "prevPage"));
        }
        if (page < setAndGet.getAssign().getPages()) {
            gui.setItem(9 * 4 + 8, createButtonItem(Material.ARROW, "Next Page", (short) 0, null, "nextPage"));
        }
    }

    private void initializeGUI(int page) {
        setAndGet.getAssign().assignPage(page);
    }

    private ItemStack createButtonItem(Material material, String name, short data, String lore, String identifier) {
        return setAndGet.getCreateButtonItem().createButtonItem(material, name, data, lore, identifier);
    }

    private void fillBorders(ItemStack borderItem, int size) {
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
        for (int i = 0; i < (size9 - 1); i++) {
            int rightSlot = 9 * (i + 2) - 1;

            gui.setItem(rightSlot, borderItem); // Right column
        }
    }

    public void openGUI(Player player) {
        generateGUI(currentPage);
        initializeGUI(currentPage);
        player.openInventory(gui);
    }

    private void handleButtonClick(Player player, String identifier, short data, String configKey, int guiIndex, String enableMessage, String disableMessage) {
        boolean isEnabled = data == 8;
        if (identifier.equals(REMOVAL_VISIBLE)) {
            config.set(configKey, isEnabled);
        } else {
            config.set(configKey, isEnabled ? 1 : 0);
        }
        configManager.saveConfig();

        gui.setItem(9 * 3 + guiIndex, createButtonItem(
                Material.INK_SACK,
                isEnabled ? DISABLE : ENABLE,
                (short) (isEnabled ? 10 : 8),
                null,
                identifier
        ));

        player.sendMessage(!isEnabled ? ChatColor.RED + disableMessage : ChatColor.GREEN + enableMessage);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (isClickEventValid(event)) {
            event.setCancelled(true);
            handleItemClick((Player) event.getWhoClicked(), event.getCurrentItem());
        }
    }

    private boolean isClickEventValid(InventoryClickEvent event) {
        return event.getInventory().equals(gui) && event.getWhoClicked() instanceof Player;
    }

    private void handleItemClick(Player player, ItemStack clickedItem) {
        if (clickedItem != null && (clickedItem.getType() == Material.INK_SACK || clickedItem.getType() == Material.PAPER || clickedItem.getType() == Material.CONCRETE || clickedItem.getType() == Material.ARROW)) {
            String identifier = NBT.get(clickedItem, (Function<ReadableItemNBT, String>) nbt -> nbt.getString("Ident"));
            short data = clickedItem.getDurability();

            if (handleButtonFeatures(player, identifier, data)) {
                return; // Button feature handling succeeded
            }

            if (clickedItem.getType() == Material.PAPER && (identifier.equalsIgnoreCase("Reset"))) {
                confirmationPrompt(player);
            }

            if (identifier.equalsIgnoreCase("nextPage")) {
                currentPage++;
                openGUI(player);
            } else if (identifier.equalsIgnoreCase("prevPage")) {
                currentPage--;
                openGUI(player);
            }
        }
    }

    private boolean handleButtonFeatures(Player player, String identifier, short data) {
        if (gui.getTitle().equals("Test (WIP) Page " + (currentPage + 1))) {
            if (identifier.equalsIgnoreCase(ENABLE_BUCKET)) {
                handleButtonClick(player, identifier, data, BUCKET_ENABLED, 1, "Liquid removal enabled!", "Liquid removal disabled!");
                return true;
            } else if (identifier.equalsIgnoreCase(ENABLE_BARRIER)) {
                handleButtonClick(player, identifier, data, BARRIER_ENABLED, 2, "Surface removal enabled!", "Surface removal disabled!");
                return true;
            } else if (identifier.equalsIgnoreCase(ENABLE_BEDROCK)) {
                handleButtonClick(player, identifier, data, BEDROCK_ENABLED, 3, "All block removal enabled!", "All block removal disabled!");
                return true;
            } else if (identifier.equalsIgnoreCase(ENABLE_SPHERE)) {
                handleButtonClick(player, identifier, data, SPHERE_ENABLED, 4, "Sphere creation enabled!", "Sphere creation disabled!");
                return true;
            } else if (identifier.equalsIgnoreCase(ENABLE_TSUNAMI)) {
                handleButtonClick(player, identifier, data, TSUNAMI_ENABLED, 5, "Tsunami enabled!", "Tsunami disabled!");
                return true;
            } else if (identifier.equalsIgnoreCase(ENABLE_CREEPER)) {
                handleButtonClick(player, identifier, data, CREEPER_ENABLED, 6, "Custom creeper explosions enabled!", "Custom creeper explosions disabled!");
                return true;
            } else if (identifier.equalsIgnoreCase(ENABLE_TNT)) {
                handleButtonClick(player, identifier, data, TNT_ENABLED, 7, "Custom TNT explosions enabled!", "Custom TNT explosions disabled!");
                return true;
            } else if (identifier.equalsIgnoreCase(REMOVAL_VISIBLE)) {
                handleButtonClick(player, identifier, data, REMOVAL_VISIBLE, 1, "Removal visibility enabled!", "Removal visibility disabled!");
            }
        } else if (gui.getTitle().equals("Reset config?")) {
            if (identifier.equalsIgnoreCase("cancel")) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "Config reset cancelled");
                return true;
            } else if (identifier.equalsIgnoreCase("confirm")) {
                resetConfig(player);
                player.closeInventory();
                return true;
            }
        }
        return false;
    }

    private void resetConfig(Player player) {
        setAndGet.getConfigManager().resetConfig(player);
    }

    private void confirmationPrompt(Player player) {
        this.gui = Bukkit.createInventory(null, 9 * 3, "Reset config?");
        fillBorders(createButtonItem(Material.STAINED_GLASS_PANE, ChatColor.RED + "" + ChatColor.BOLD + "Warning", (short) 14, null, null), 9 * 3);
        gui.setItem(9 + 2, createButtonItem(Material.CONCRETE, ChatColor.RED + "Cancel", (short) 14, null, "cancel"));
        gui.setItem(9 + 6, createButtonItem(Material.CONCRETE, ChatColor.GREEN + "Confirm", (short) 13, null, "confirm"));
        player.openInventory(gui);
    }

    public Inventory getGui() {
        return gui;
    }
}