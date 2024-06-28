package org.gecko.spigotadmintoys.gui.logic;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteItemNBT;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class CreateButtonItem {

    public ItemStack createButtonItem(Material material, String name, short data, String lore, String ident) {
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

        NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setString("Ident", ident));

        return item;
    }
}
