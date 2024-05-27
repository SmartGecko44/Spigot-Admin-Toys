package org.gecko.spigotadmintoys.gui.presets.config;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.gecko.spigotadmintoys.logic.SetAndGet;

import static org.gecko.spigotadmintoys.gui.ConfigGUI.*;

public class Assign {
    private final SetAndGet setAndGet;
    private FileConfiguration config;
    private Inventory gui;
    private int playerLimit;
    private int creeperLimit;
    private int tntLimit;
    private final int pages = 1;

    public Assign(SetAndGet setAndGet) {
        this.setAndGet = setAndGet;
    }

    public void assignPage(int page) {
        this.config = setAndGet.getConfigManager().getConfig();
        this.gui = setAndGet.getConfigGUI().getGui();
        this.playerLimit = setAndGet.getRadiusLimit() - 2;
        this.creeperLimit = setAndGet.getCreeperRadiusLimit() - 2;
        this.tntLimit = setAndGet.getTntRadiusLimit() - 2;
        switch (page) {
            case 0:
                gui1();
                break;
            case 1:
                gui2();
                break;
        }
        gui.setItem(8, createButtonItem(Material.PAPER, ChatColor.RESET + "" + ChatColor.RED + "Reset config", (short) 0, null, "Reset"));
    }

    private ItemStack createButtonItem(Material material, String name, short data, String lore, String ident) {
        return setAndGet.getCreateButtonItem().createButtonItem(material, name, data, lore, ident);
    }

    public void gui1() {
        gui.setItem(9 + 1, createButtonItem(Material.BUCKET, ChatColor.RESET + "Liquid removal", (short) 0, null, null));
        gui.setItem(9 * 2 + 1, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(playerLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + MANAGED, null));

        gui.setItem(9 + 2, createButtonItem(Material.BARRIER, ChatColor.RESET + "Surface removal", (short) 0, null, null));
        gui.setItem(9 * 2 + 2, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(playerLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + MANAGED, null));

        gui.setItem(9 + 3, createButtonItem(Material.BEDROCK, ChatColor.RESET + "All block removal", (short) 0, null, null));
        gui.setItem(9 * 2 + 3, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(playerLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + MANAGED, null));

        gui.setItem(9 + 4, createButtonItem(Material.FLOWER_POT_ITEM, ChatColor.RESET + "Sphere creation", (short) 0, null, null));
        gui.setItem(9 * 2 + 4, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(playerLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + MANAGED, null));

        gui.setItem(9 + 5, createButtonItem(Material.WATER_BUCKET, ChatColor.RESET + "Tsunami", (short) 0, null, null));
        gui.setItem(9 * 2 + 5, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(playerLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + MANAGED, null));

        gui.setItem(9 + 6, createButtonItem(Material.SKULL_ITEM, ChatColor.RESET + "Custom creeper explosions", (short) 4, null, null));
        gui.setItem(9 * 2 + 6, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(creeperLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "This value is managed by the creeper radius limit.", null));

        gui.setItem(9 + 7, createButtonItem(Material.TNT, ChatColor.RESET + "Custom TNT explosions", (short) 0, null, null));
        gui.setItem(9 * 2 + 7, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + String.valueOf(tntLimit), (short) 0, ChatColor.RESET + "" + ChatColor.DARK_PURPLE + "This value is managed by the TNT radius limit.", null));


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

        if (config.getInt(SPHERE_ENABLED) == 1) {
            gui.setItem(9 * 3 + 4, createButtonItem(Material.INK_SACK, DISABLE, (short) 10, null, ENABLE_SPHERE));
        } else {
            gui.setItem(9 * 3 + 4, createButtonItem(Material.INK_SACK, ENABLE, (short) 8, null, ENABLE_SPHERE));
        }

        if (config.getInt(TSUNAMI_ENABLED) == 1) {
            gui.setItem(9 * 3 + 5, createButtonItem(Material.INK_SACK, DISABLE, (short) 10, null, ENABLE_TSUNAMI));
        } else {
            gui.setItem(9 * 3 + 5, createButtonItem(Material.INK_SACK, ENABLE, (short) 8, null, ENABLE_TSUNAMI));
        }

        if (config.getInt(CREEPER_ENABLED) == 1) {
            gui.setItem(9 * 3 + 6, createButtonItem(Material.INK_SACK, DISABLE, (short) 10, null, ENABLE_CREEPER));
        } else {
            gui.setItem(9 * 3 + 6, createButtonItem(Material.INK_SACK, ENABLE, (short) 8, null, ENABLE_CREEPER));
        }

        if (config.getInt(TNT_ENABLED) == 1) {
            gui.setItem(9 * 3 + 7, createButtonItem(Material.INK_SACK, DISABLE, (short) 10, null, ENABLE_TNT));
        } else {
            gui.setItem(9 * 3 + 7, createButtonItem(Material.INK_SACK, ENABLE, (short) 8, null, ENABLE_TNT));
        }
    }

    private void gui2() {
        gui.setItem(9 + 1, createButtonItem(Material.BEACON, ChatColor.RESET + "Removal visibility", (short) 0, null, null));
        gui.setItem(9 * 2 + 1, createButtonItem(Material.ENDER_PEARL, ChatColor.RESET + (!setAndGet.getShowRemoval() ? ChatColor.RED + "Disabled" : ChatColor.GREEN + "Enabled"), (short) 0, null, null));
        gui.setItem(9 * 3 + 1, createButtonItem(Material.INK_SACK, (setAndGet.getShowRemoval() ? DISABLE : ENABLE), (short) (setAndGet.getShowRemoval() ? 10 : 8), null, REMOVAL_VISIBLE));
    }

    public int getPages() {
        return pages;
    }
}
