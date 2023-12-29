package org.gecko.wauh.items.blocks;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.gecko.wauh.items.TriggerItems;

public class MirrorItem {

    private final TriggerItems triggerItems = new TriggerItems();

    public ItemStack createMirrorItem() {
        return triggerItems.createCustomItem(Material.GLASS, "Mirror (WIP)", (short) 0, "WIP", "MirrorItem");
    }

}
