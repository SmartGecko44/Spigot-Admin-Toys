package org.gecko.spigotadmintoys.items;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteItemNBT;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class TriggerItems {

    public ItemStack createCustomItem(Material material, String name, short data, String lore, String ident) {
        ItemStack item = new ItemStack(material, 1, data);
        name = ChatColor.RESET + name;
        lore = ChatColor.RESET + "" + ChatColor.DARK_PURPLE + lore;
        List<String> loreToList = Collections.singletonList(lore);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(loreToList);
        item.setItemMeta(meta);

        NBT.modify(item, (Consumer<ReadWriteItemNBT>) nbt -> nbt.setString("Ident", ident));

        return item;
    }
}
